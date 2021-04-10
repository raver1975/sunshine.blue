package com.klemstinegroup.sunshinelab.client;

import com.badlogic.gdx.utils.Base64Coder;
import com.klemstinegroup.sunshinelab.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSFileListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.NativeIPFSInterface;

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
      if(!$wnd.node){
          console.log("Node not running!");
      }
      else{
          $wnd.node.cat(cid).next().then(function(chunk){
            var b64encoded = btoa(chunk.value);
            self.@com.klemstinegroup.sunshinelab.client.NativeIPFSgwt::finishDownload(Ljava/lang/String;)(b64encoded);
          });
      }
    }-*/;

    native void uploadToIPFS(String base64, String content)/*-{
      var self = this;
      var byteCharacters = atob(base64);
      var byteNumbers = new Array(byteCharacters.length);
      for (var i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      var byteArray = new Uint8Array(byteNumbers);
      if(!$wnd.node){
          console.log("Node not running!");
      }
      else{
          $wnd.node.add(byteArray).then(function(fileAdded){
          console.log('Added file:', fileAdded.path);
          self.@com.klemstinegroup.sunshinelab.client.NativeIPFSgwt::finishUpload(Ljava/lang/String;)(fileAdded.path);
      });
      }
    }-*/;

    public void finishUpload(String cid) {
        IPFSUtils.pinFile(cid);
        uploadListener.cid(cid);
    }

    public void finishDownload(String base64) {
        byte[] b = Base64Coder.decode(base64);
        downloadListener.downloaded(b);
    }
}
