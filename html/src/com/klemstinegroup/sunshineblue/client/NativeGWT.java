package com.klemstinegroup.sunshineblue.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Timer;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.FontObject;
import com.klemstinegroup.sunshineblue.engine.overlays.FontOverlay;
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSFileListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSUtils;
import com.klemstinegroup.sunshineblue.engine.util.NativeInterface;

import java.util.TimerTask;

public class NativeGWT implements NativeInterface {
    ArrayMap<Integer, IPFSCIDListener> uploadListener = new ArrayMap<>();
    ArrayMap<Integer, IPFSFileListener> downloadListener = new ArrayMap<>();
    private boolean succeed;

    public NativeGWT() {
        addpubsublistener();
    }

    native void addpubsublistener()/*-{
    function Utf8ArrayToStr(array) {
    var out, i, len, c;
    var char2, char3;

    out = "";
    len = array.length;
    i = 0;
    while(i < len) {
    c = array[i++];
    switch(c >> 4)
    {
      case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
        // 0xxxxxxx
        out += String.fromCharCode(c);
        break;
      case 12: case 13:
        // 110x xxxx   10xx xxxx
        char2 = array[i++];
        out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
        break;
      case 14:
        // 1110 xxxx  10xx xxxx  10xx xxxx
        char2 = array[i++];
        char3 = array[i++];
        out += String.fromCharCode(((c & 0x0F) << 12) |
                       ((char2 & 0x3F) << 6) |
                       ((char3 & 0x3F) << 0));
        break;
    }
    }

    return out;
}
     function run(){
        try{
          console.log("subscribing");
          $wnd.node.pubsub.subscribe("sunshine.blue",function(msg){
              @com.klemstinegroup.sunshineblue.engine.util.SerializeUtil::infromGWTotherCID(Ljava/lang/String;)(Utf8ArrayToStr(msg.data));
          }).then();
        }
        catch (e){
         setTimeout(run,1000);
        }
     }
     run();

    }-*/;

    @Override
    public void uploadIPFS(byte[] data, IPFSCIDListener listener) {
/*        int j = MathUtils.random.nextInt();
        this.uploadListener.put(j, listener);
        uploadToIPFS(new String(Base64Coder.encode(data)), j);*/

        //pin through ipfs gateway, otherwise gets lost because web ipfs nodes do not persist.
        IPFSUtils.uploadFile(data,listener);
    }

    @Override
    public void downloadIPFS(String cid, IPFSFileListener listener) {
        if (cid == null || cid.isEmpty() || !cid.startsWith("Q")) {
            return;
        }
        int j = MathUtils.random.nextInt();
        downloadListener.put(j, listener);
        Gdx.app.log("requesting", cid);
        downloadFromIPFS(cid, j);
    }

    @Override
    public void downloadFile(String url, boolean cors, IPFSFileListener listener) {
        int j = MathUtils.random.nextInt();
        Gdx.app.log("downloadind:", url + "\t" + j);
        downloadListener.put(j, listener);
        downloadFromNet((cors ? Statics.CORSGateway : "") + url, j);
    }

    @Override
    public void downloadPixmap(String url, Pixmap.DownloadPixmapResponseListener listener) {
        final Image img;
        if (url.contains(Statics.IPFSGateway)) {
            img = new Image(url);
        } else {
            img = new Image(Statics.CORSGateway + url);
        }

        ImageElement.as(img.getElement()).setAttribute("crossorigin", "anonymous");
        final RootPanel root = RootPanel.get("embed-image");

        root.add(img);
        img.setVisible(false);
        Timer.Task tt = new Timer.Task() {
            @Override
            public void run() {
                if (!succeed) {
                    listener.downloadFailed(new Throwable("img download error"));
                }
            }
        };
        Timer.schedule(tt, 5);
        succeed = false;
        img.addLoadHandler(new LoadHandler() {


            @Override
            public void onLoad(LoadEvent event) { 
                succeed = true;
                tt.cancel();
                listener.downloadComplete(new Pixmap(ImageElement.as(img.getElement())));
            }
        });
    }

    @Override
    public void doneSavingScene(String cid, String screenshot) {
        Gdx.app.log("done", "done saving scene");
        doneSavingSceneJS(cid, screenshot);
    }

    @Override
    public void openKeyboard() {
        keyboard();
    }

    public native void keyboard() /*-{
    var self=this;
    console.log("keyboard open");
      var text=$wnd.prompt("Text:");
      self.@com.klemstinegroup.sunshineblue.client.NativeGWT::setText(Ljava/lang/String;)(text);
    }-*/;

    public void setText(String text) {
        ((FontObject) SunshineBlue.instance.FONT_OVERLAY.fontObject).fd.text = text;
    }


    native void doneSavingSceneJS(String cid, String screenshot) /*-{
        $wnd.onpopstate=function(event){
            var currentState = event.state;
            console.log("onpopstate:"+currentState.cid);
            @com.klemstinegroup.sunshineblue.engine.util.SerializeUtil::infromGWT(Ljava/lang/String;)(currentState.cid);
//            $doc.body.innerHTML = currentState.innerhtml;

        }
        console.log("changing url:"+cid);
//        var stateObj = { url: settings.url, innerhtml: document.body.innerHTML,cid:cid };
        var stateObj = {cid:cid,screenshot:screenshot };
        $wnd.history.pushState( stateObj, 'sunshine.blue', '/?'+cid );


             function run(){
        try{
          console.log("publishing:"+cid);
          $wnd.node.pubsub.publish("sunshine.blue", cid+","+screenshot).then();
        }
        catch (e){
         setTimeout(run,1000);
        }
     }
     run();


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
     function ggg(cat,chunks){
            cat.next().then(function(chunk){
               if (chunk.done){
                self.@com.klemstinegroup.sunshineblue.client.NativeGWT::finishDownload(Ljava/lang/String;I)(toBase64(chunks),iii);
                delete chunks;
               }
               else{
                 var chunv=chunk.value;
                 var c=new Uint8Array(chunks.length +chunv.length);
                 c.set(chunks);
                 c.set(chunv,chunks.length);
                 setTimeout(function(){ggg(cat,c);},200);
               }

            });
         }

    function run(cid1){
      console.log("loading1 cid:"+cid1);
      if(!($wnd.node&& $wnd.node.isOnline())){
          console.log("Node not running!");
          setTimeout(function(){run(cid1);},1000);
      }

      else{
         $wnd.node.pin.add(cid1).then();
         ggg($wnd.node.cat(cid1),new Uint8Array());
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
          $wnd.node.pin.add(fileAdded.path).then();
          self.@com.klemstinegroup.sunshineblue.client.NativeGWT::finishUpload(Ljava/lang/String;I)(fileAdded.path,iii);
      });
      }
      };
      run(base64);
    }-*/;

    public void finishUpload(String cid, int i) {
        Gdx.app.log("upload", cid + "\t" + i);
        uploadListener.get(i).cid(cid);
        uploadListener.removeKey(i);
    }

    public void finishDownload(String base64, int i) {
        byte[] b = Base64Coder.decode(base64);
        Gdx.app.log("download", base64.substring(0, 10) + "\t" + i + "\tsize:" + b.length);
        downloadListener.get(i).downloaded(b);
        downloadListener.removeKey(i);
    }
}
