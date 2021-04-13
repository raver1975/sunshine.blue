package com.klemstinegroup.sunshinelab.client;

import com.badlogic.gdx.utils.Base64Coder;
import com.klemstinegroup.sunshinelab.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSFileListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.NativeIPFSInterface;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class NativeIPFSgwt implements NativeIPFSInterface {
    IPFSCIDListener uploadListener;
    IPFSFileListener downloadListener;

    @Override
    public void uploadFile(byte[] data, String mime, IPFSCIDListener listener) {
        this.uploadListener = listener;
        uploadToIPFS(new String(Base64Coder.encode(data)), mime);
    }

    @Override
    public void downloadFile(String cid, IPFSFileListener listener) {
        downloadListener = listener;
        downloadFromIPFS(cid);
    }

    native void downloadFromIPFS(String cid)/*-{
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
            console.log(base64encoded);
            console.log(chunk);
            self.@com.klemstinegroup.sunshinelab.client.NativeIPFSgwt::finishDownload(Ljava/lang/String;)(base64encoded);
          });
      }
      };
      run(cid);
    }-*/;

    native void uploadToIPFS(String base64, String content)/*-{
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
          self.@com.klemstinegroup.sunshinelab.client.NativeIPFSgwt::finishUpload(Ljava/lang/String;)(fileAdded.path);
      });
      }
      };
      run(base64,content);
    }-*/;

    public void finishUpload(String cid) {
        IPFSUtils.pinFile(cid);
        uploadListener.cid(cid);
    }

    public void finishDownload(String base64) {
       byte[] b=Base64Coder.decode(base64);
        downloadListener.downloaded(b);
    }
}
