package ar.com.hjg.pngj;

import java.util.logging.Logger;

import ar.com.hjg.pngj.chunks.ChunkRaw;

/**
 * Parses a PNG chunk, consuming bytes in one of three modes:
 * {@link ChunkReaderMode#BUFFER}, {@link ChunkReaderMode#PROCESS},
 * {@link ChunkReaderMode#SKIP}.
 * <p>
 * It calls {@link #chunkDone()} when done. Also calls
 * {@link #processData(byte[], int, int)} if <code>PROCESS</code> mode. Apart
 * from thas, it's totally agnostic (it doesn't know about IDAT chunks, or PNG
 * general structure)
 * <p>
 * The object wraps a <tt>ChunkRaw</tt> instance (content allocated and filled
 * only if BUFFER mode). It should be short lived (one instance created for each
 * chunk, and discarded after reading), but the wrapped <tt>chunkRaw</tt> can be
 * (usually is) long lived.
 */
public abstract class ChunkReader implements IBytesConsumer {
	private static final Logger LOGGER = Logger.getLogger(ChunkReader.class.getName());

	/**
	 * see {@link ChunkReaderMode}
	 */
	public final ChunkReaderMode mode;
	private final ChunkRaw chunkRaw;

	/**
	 * How many bytes have been read for this chunk, data only
	 */
	protected int read = 0;
	private int crcn = 0; // how many bytes have been read from crc

	private boolean crcCheck; // by default, this is false for SKIP, true elsewhere
	protected ErrorBehaviour errorBehav = ErrorBehaviour.STRICT;

	/**
	 * Modes of ChunkReader chunk processing.
	 */
	public enum ChunkReaderMode {
		/**
		 * Stores full chunk data in buffer
		 */
		BUFFER,
		/**
		 * Does not store content, processes on the fly, calling processData()
		 * for each partial read
		 */
		PROCESS,
		/**
		 * Does not store nor process - implies crcCheck=false (by default).
		 */
		SKIP
	}

	/**
	 * The constructor creates also a chunkRaw, preallocated if mode =
	 * ChunkReaderMode.BUFFER
	 * 
	 * @param clen
	 * @param id
	 * @param offsetInPng
	 *            Informational, is stored in chunkRaw
	 * @param mode
	 */
	public ChunkReader(int clen, String id, long offsetInPng, ChunkReaderMode mode) {
		if (mode == null || id.length() != 4 || clen < 0)
			throw new PngjInputException("Bad chunk paramenters: " + mode);
		this.mode = mode;
		chunkRaw = new ChunkRaw(clen, id, mode == ChunkReaderMode.BUFFER);
		chunkRaw.setOffset(offsetInPng);
		this.crcCheck = mode != ChunkReaderMode.SKIP; // can be changed with setter
		// PngHelperInternal.debug("ChunkReader " + this.getClass() + " id="+id + " mode:"+mode);
	}

	/**
	 * Returns raw chunk (data can be empty or not, depending on
	 * ChunkReaderMode)
	 * 
	 * @return Raw chunk - never null
	 */
	public ChunkRaw getChunkRaw() {
		return chunkRaw;
	}

	/**
	 * Consumes data for the chunk (data and CRC). This only consumes bytes
	 * owned by this chunk (data + crc , not id+len prefix)
	 * 
	 * In ChunkReaderMode.PROCESS can call processData() (not more than once)
	 * 
	 * If this ends the chunk (included CRC) it checks CRC (if checking) and
	 * calls chunkDone()
	 * 
	 * @param buf
	 * @param off
	 * @param len
	 * @return How many bytes have been consumed
	 */
	public final int consume(byte[] buf, int off, int len) {
		if (len == 0)
			return 0;
		if (len < 0)
			throw new PngjException("negative length??");
		if (read == 0 && crcn == 0 && crcCheck)
			chunkRaw.updateCrc(chunkRaw.idbytes, 0, 4); // initializes crc calculation with the Chunk ID
		int bytesForData = chunkRaw.len - read; // bytesForData : bytes to be actually read from chunk data
		if (bytesForData > len)
			bytesForData = len;
		// we want to call processData even for empty chunks (IEND:bytesForData=0) at
		// least once
		if (bytesForData > 0 || crcn == 0) {
			// in buffer mode we compute the CRC at the end
			if (crcCheck && mode != ChunkReaderMode.BUFFER && bytesForData > 0)
				chunkRaw.updateCrc(buf, off, bytesForData);

			if (mode == ChunkReaderMode.BUFFER) {
				// just copy the contents to the internal buffer
				if (chunkRaw.data != buf && bytesForData > 0) {
					// If the buffer passed if the same as this one, we don't copy.
					// The caller should know what he's doing
					System.arraycopy(buf, off, chunkRaw.data, read, bytesForData);
				}
			} else if (mode == ChunkReaderMode.PROCESS) {
				processData(read, buf, off, bytesForData);
			} else {
				// mode == ChunkReaderMode.SKIP; nothing to do
			}
			read += bytesForData;
			off += bytesForData;
			len -= bytesForData;
		}
		int crcRead = 0;
		if (read == chunkRaw.len) { // data done - read crc?
			crcRead = 4 - crcn;
			if (crcRead > len)
				crcRead = len;
			if (crcRead > 0) {
				if (buf != chunkRaw.crcval)
					System.arraycopy(buf, off, chunkRaw.crcval, crcn, crcRead);
				crcn += crcRead;
				if (crcn == 4) {
					if (crcCheck) {
						if (mode == ChunkReaderMode.BUFFER) { // in buffer mode we compute the CRC on one single call
							chunkRaw.updateCrc(chunkRaw.data, 0, chunkRaw.len);
						}
						chunkRaw.checkCrc(errorBehav == ErrorBehaviour.STRICT);
					}
					LOGGER.fine("Chunk done");
					chunkDone();
				}
			}
		}
		return bytesForData > 0 || crcRead > 0 ? bytesForData + crcRead : -1 /* should not happen */ ;
	}

	/**
	 * Chunks has been read
	 * 
	 * @return true if we have read all chunk, including trailing CRC
	 */
	public final boolean isDone() {
		return crcn == 4; // has read all 4 bytes from the crc
	}

	/**
	 * Determines if CRC should be checked. This should be called before
	 * starting reading.
	 * 
	 * @see also #setErrorBehav(ErrorBehaviour)
	 * 
	 * @param crcCheck
	 */
	public void setCrcCheck(boolean crcCheck) {
		if (read != 0 && crcCheck && !this.crcCheck)
			throw new PngjException("too late!");
		this.crcCheck = crcCheck;
	}

	/**
	 * This method will only be called in PROCESS mode, probably several times,
	 * each time with a new fragment of data read from inside the chunk. For
	 * chunks with zero-length data, this will still be called once.
	 * 
	 * It's guaranteed that the data corresponds exclusively to this chunk data
	 * (no crc, no data from no other chunks, )
	 * 
	 * @param offsetInchunk
	 *            data bytes that had already been read/processed for this chunk
	 * @param buf
	 * @param off
	 * @param len
	 */
	protected abstract void processData(int offsetInchunk, byte[] buf, int off, int len);

	/**
	 * This method will be called (in all modes) when the full chunk -including
	 * crc- has been read
	 */
	protected abstract void chunkDone();

	public boolean isFromDeflatedSet() {
		return false;
	}

	public ErrorBehaviour getErrorBehav() {
		return errorBehav;
	}

	/** see also {@link #setCrcCheck(boolean)} **/
	public void setErrorBehav(ErrorBehaviour errorBehav) {
		this.errorBehav = errorBehav;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkRaw == null) ? 0 : chunkRaw.hashCode());
		return result;
	}

	/**
	 * Equality (and hash) is basically delegated to the ChunkRaw
	 */
	@Override
	public boolean equals(Object obj) { // delegates to chunkraw
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkReader other = (ChunkReader) obj;
		if (chunkRaw == null) {
			return other.chunkRaw == null;
		} else return chunkRaw.equals(other.chunkRaw);
	}

	@Override
	public String toString() {
		return chunkRaw.toString();
	}

}
