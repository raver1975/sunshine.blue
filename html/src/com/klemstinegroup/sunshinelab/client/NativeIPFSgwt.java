package com.klemstinegroup.sunshinelab.client;

import com.badlogic.gdx.utils.Base64Coder;
import com.klemstinegroup.sunshinelab.engine.util.IPFSResponseListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.NativeIPFSInterface;

public class NativeIPFSgwt implements NativeIPFSInterface {
    IPFSResponseListener listener;
    @Override
    public void uploadFile(byte[] data, String mime, IPFSResponseListener listener) {
        this.listener=listener;
        uploadToIPFS(new String(Base64Coder.encode(data)),mime);
    }

    native void uploadToIPFS(String base64,String content)/*-{
      var self = this;
      var byteCharacters = atob(base64);
      var byteNumbers = new Array(byteCharacters.length);
      for (var i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      var byteArray = new Uint8Array(byteNumbers);
      $wnd.node.add(byteArray).then(function(fileAdded){
        console.log('Added file:', fileAdded.path);
        self.@com.klemstinegroup.sunshinelab.client.NativeIPFSgwt::finish(Ljava/lang/String;)(fileAdded.path);
      });
    }-*/;

    public void finish(String cid){
        IPFSUtils.pinFile(cid);
        listener.cid(cid);
    }
}
