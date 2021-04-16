package com.klemstinegroup.sunshinelab.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Base64Coder;
import com.klemstinegroup.sunshinelab.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSFileListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.NativeNetworkInterface;

import java.util.Arrays;

public class NativeNetworkGWT implements NativeNetworkInterface {
    ArrayMap<Integer,IPFSCIDListener> uploadListener=new ArrayMap<>();
    ArrayMap<Integer,IPFSFileListener> downloadListener=new ArrayMap<>();
    int cnt=0;
    @Override
    public void uploadIPFS(byte[] data, String mime, IPFSCIDListener listener) {
        int j=cnt++;
        this.uploadListener.put(j,listener);
        uploadToIPFS(new String(Base64Coder.encode(data)), mime,j);
    }

    @Override
    public void downloadIPFS(String cid, IPFSFileListener listener) {
        int j=cnt++;
        downloadListener.put(j,listener);
        downloadFromIPFS(cid,j);
    }

    @Override
    public void downloadFile(String url, IPFSFileListener listener) {
        int j=cnt++;
        Gdx.app.log("downloadind:",url+"\t"+j);
        downloadListener.put(j,listener);
        downloadFromNet(url,j);
    }

    native void downloadFromNet(String url, int iii)/*-{
    var self=this;

var encoder = new TextEncoder("ascii");
var decoder = new TextDecoder("ascii");
var base64Table = encoder.encode('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=');

function toBase64(arrayBuffer) {
  var base64    = ''
  var encodings = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'

  var bytes         = new Uint8Array(arrayBuffer)
  var byteLength    = bytes.byteLength
  var byteRemainder = byteLength % 3
  var mainLength    = byteLength - byteRemainder

  var a, b, c, d
  var chunk

  // Main loop deals with bytes in chunks of 3
  for (var i = 0; i < mainLength; i = i + 3) {
    // Combine the three bytes into a single integer
    chunk = (bytes[i] << 16) | (bytes[i + 1] << 8) | bytes[i + 2]

    // Use bitmasks to extract 6-bit segments from the triplet
    a = (chunk & 16515072) >> 18 // 16515072 = (2^6 - 1) << 18
    b = (chunk & 258048)   >> 12 // 258048   = (2^6 - 1) << 12
    c = (chunk & 4032)     >>  6 // 4032     = (2^6 - 1) << 6
    d = chunk & 63               // 63       = 2^6 - 1

    // Convert the raw binary segments to the appropriate ASCII encoding
    base64 += encodings[a] + encodings[b] + encodings[c] + encodings[d]
  }

  // Deal with the remaining bytes and padding
  if (byteRemainder == 1) {
    chunk = bytes[mainLength]

    a = (chunk & 252) >> 2 // 252 = (2^6 - 1) << 2

    // Set the 4 least significant bits to zero
    b = (chunk & 3)   << 4 // 3   = 2^2 - 1

    base64 += encodings[a] + encodings[b] + '=='
  } else if (byteRemainder == 2) {
    chunk = (bytes[mainLength] << 8) | bytes[mainLength + 1]

    a = (chunk & 64512) >> 10 // 64512 = (2^6 - 1) << 10
    b = (chunk & 1008)  >>  4 // 1008  = (2^6 - 1) << 4

    // Set the 2 least significant bits to zero
    c = (chunk & 15)    <<  2 // 15    = 2^4 - 1

    base64 += encodings[a] + encodings[b] + encodings[c] + '='
  }

  return base64
}
    fetch(url)
    .then(function(response){return response.arrayBuffer();})
    .then(function(buffer) {
       console.log(buffer);
      var base64encoded=toBase64(buffer);
        self.@com.klemstinegroup.sunshinelab.client.NativeNetworkGWT::finishDownload(Ljava/lang/String;I)(base64encoded,iii);
    })
    ['catch'](function(error){
         console.log(error);
    });

    }-*/;

    native void downloadFromIPFS(String cid, int iii)/*-{
    var self=this;
var encoder = new TextEncoder("ascii");
var decoder = new TextDecoder("ascii");
var base64Table = encoder.encode('ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=');

function toBase64(dataArr){
    var padding = dataArr.byteLength % 3;
    var len = dataArr.byteLength - padding;
    padding = padding > 0 ? (3 - padding) : 0;
    var outputLen = ((len/3) * 4) + (padding > 0 ? 4 : 0);
    var output = new Uint8Array(outputLen);
    var outputCtr = 0;
    for(var i=0; i<len; i+=3){
        var buffer = ((dataArr[i] & 0xFF) << 16) | ((dataArr[i+1] & 0xFF) << 8) | (dataArr[i+2] & 0xFF);
        output[outputCtr++] = base64Table[buffer >> 18];
        output[outputCtr++] = base64Table[(buffer >> 12) & 0x3F];
        output[outputCtr++] = base64Table[(buffer >> 6) & 0x3F];
        output[outputCtr++] = base64Table[buffer & 0x3F];
    }
    if (padding == 1) {
        var buffer = ((dataArr[len] & 0xFF) << 8) | (dataArr[len+1] & 0xFF);
        output[outputCtr++] = base64Table[buffer >> 10];
        output[outputCtr++] = base64Table[(buffer >> 4) & 0x3F];
        output[outputCtr++] = base64Table[(buffer << 2) & 0x3F];
        output[outputCtr++] = base64Table[64];
    } else if (padding == 2) {
        var buffer = dataArr[len] & 0xFF;
        output[outputCtr++] = base64Table[buffer >> 2];
        output[outputCtr++] = base64Table[(buffer << 4) & 0x3F];
        output[outputCtr++] = base64Table[64];
        output[outputCtr++] = base64Table[64];
    }

    var ret = decoder.decode(output);
    output = null;
    dataArr = null;
    return ret;
}

    function run(cid){
      if(!($wnd.node&& $wnd.node.isOnline())){
          console.log("Node not running!");
          setTimeout(run,1000,cid);
      }
      else{
          $wnd.node.cat(cid).next().then(function(chunk){
            var base64encoded=toBase64(chunk.value);
            self.@com.klemstinegroup.sunshinelab.client.NativeNetworkGWT::finishDownload(Ljava/lang/String;I)(base64encoded,iii);
          });
      }
      };
      run(cid);
    }-*/;

    native void uploadToIPFS(String base64, String content, int iii)/*-{
      var self = this;
      function run(base64,content){
      var byteCharacters = atob(base64);
      var byteNumbers = new Array(byteCharacters.length);
      for (var i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      var byteArray = new Uint8Array(byteNumbers);
      if(!($wnd.node && $wnd.node.isOnline()) ){
          console.log("Node not running!");
          setTimeout(run,1000,base64,content);
      }
      else{
          $wnd.node.add(byteArray).then(function(fileAdded){
          console.log('Added file:', fileAdded.path);
          self.@com.klemstinegroup.sunshinelab.client.NativeNetworkGWT::finishUpload(Ljava/lang/String;I)(fileAdded.path,iii);
      });
      }
      };
      run(base64,content);
    }-*/;

    public void finishUpload(String cid,int i) {
        Gdx.app.log("upload",cid+"\t"+i);
        IPFSUtils.pinFile(cid);
        uploadListener.get(i).cid(cid);
        uploadListener.removeKey(i);
    }

    public void finishDownload(String base64,int i) {
        Gdx.app.log("download",base64.substring(0,10)+"\t"+i);
        byte[] b = Base64Coder.decode(base64);
        downloadListener.get(i).downloaded(b);
        downloadListener.removeKey(i);
    }
}