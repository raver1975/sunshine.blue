package com.klemstinegroup.sunshineblue.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Base64Coder;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSFileListener;
import com.klemstinegroup.sunshineblue.engine.util.NativeInterface;

public class NativeGWT implements NativeInterface {
    ArrayMap<Integer,IPFSCIDListener> uploadListener=new ArrayMap<>();
    ArrayMap<Integer,IPFSFileListener> downloadListener=new ArrayMap<>();
    @Override
    public void uploadIPFS(byte[] data, IPFSCIDListener listener) {
        int j= MathUtils.random.nextInt() ;
        this.uploadListener.put(j,listener);
        uploadToIPFS(new String(Base64Coder.encode(data)),j);
    }

    @Override
    public void downloadIPFS(String cid, IPFSFileListener listener) {
        if (cid==null||cid.isEmpty()||!cid.startsWith("Q")){
            return;
        }
        int j=MathUtils.random.nextInt();
        downloadListener.put(j,listener);
        Gdx.app.log("requesting",cid);
        downloadFromIPFS(cid,j);
    }

    @Override
    public void downloadFile(String url, boolean cors, IPFSFileListener listener) {
        int j=MathUtils.random.nextInt();
        Gdx.app.log("downloadind:",url+"\t"+j);
        downloadListener.put(j,listener);
        downloadFromNet((cors?Statics.CORSGateway:"")+url,j);
    }

    @Override
    public void downloadPixmap(String url, Pixmap.DownloadPixmapResponseListener listener) {
        final Image img = new Image(Statics.CORSGateway+url);
        ImageElement.as(img.getElement()).setAttribute("crossorigin","anonymous");
        final RootPanel root = RootPanel.get("embed-image");

        root.add(img);
        img.setVisible(false);
        img.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                listener.downloadComplete(new Pixmap(ImageElement.as(img.getElement())));

            }
        });

    }

    @Override
    public void doneSavingScene(String cid) {
        Gdx.app.log("done","done saving scene");
     doneSavingSceneJS(cid);
    }

    native void doneSavingSceneJS(String cid) /*-{
        $wnd.onpopstate=function(event){
            var currentState = event.state;
            console.log("onpopstate:"+currentState.cid);
            @com.klemstinegroup.sunshineblue.engine.util.SerializeUtil::infromGWT(Ljava/lang/String;)(currentState.cid);
//            $doc.body.innerHTML = currentState.innerhtml;

        }
        console.log("changing url:"+cid);
//        var stateObj = { url: settings.url, innerhtml: document.body.innerHTML,cid:cid };
        var stateObj = {cid:cid };
        $wnd.history.pushState( stateObj, 'sunshine.blue', '/?'+cid );
    }-*/;

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
        self.@com.klemstinegroup.sunshineblue.client.NativeGWT::finishDownload(Ljava/lang/String;I)(base64encoded,iii);
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

    function run(cid1){
      console.log("loading1 cid:"+cid1);
      if(!($wnd.node&& $wnd.node.isOnline())){
          console.log("Node not running!");
          setTimeout(function(){run(cid1);},1000);
      }

      else{
          $wnd.node.cat(cid1).next().then(function(chunk){
            var base64encoded=toBase64(chunk.value);
            self.@com.klemstinegroup.sunshineblue.client.NativeGWT::finishDownload(Ljava/lang/String;I)(base64encoded,iii);
          });
      }
      };
      run(cid);
    }-*/;

    native void uploadToIPFS(String base64, int iii)/*-{
      var self = this;
      function run(base64){
      var byteCharacters = atob(base64);
      var byteNumbers = new Array(byteCharacters.length);
      for (var i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      var byteArray = new Uint8Array(byteNumbers);
      if(!($wnd.node && $wnd.node.isOnline()) ){
          console.log("Node not running!");
          setTimeout(function(){run(base64);},1000);
      }
      else{
          $wnd.node.add(byteArray).then(function(fileAdded){
          console.log('Added file:', fileAdded.path);
          self.@com.klemstinegroup.sunshineblue.client.NativeGWT::finishUpload(Ljava/lang/String;I)(fileAdded.path,iii);
      });
      }
      };
      run(base64);
    }-*/;

    public void finishUpload(String cid,int i) {
        Gdx.app.log("upload",cid+"\t"+i);
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
