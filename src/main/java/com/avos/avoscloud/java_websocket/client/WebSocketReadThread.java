package com.avos.avoscloud.java_websocket.client;

import com.avos.avoscloud.java_websocket.WebSocketImpl;
import com.avos.avoscloud.java_websocket.WebSocketListener;
import com.avos.avoscloud.java_websocket.framing.CloseFrame;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by lbt05 on 5/24/16.
 */
class WebSocketReadThread implements Runnable {
  WebSocketImpl engine;
  InputStream istream;
  WebSocketListener wsl;

  public WebSocketReadThread(WebSocketListener wsl, WebSocketImpl engine, InputStream istream) {
    this.engine = engine;
    this.istream = istream;
    this.wsl = wsl;
  }

  @Override
  public void run() {
    byte[] rawbuffer = new byte[WebSocketImpl.RCVBUF];
    int readBytes;
    Thread.currentThread().setName("WebsocketReadThread");
    try {
      while (!Thread.interrupted() &&
          !engine.isClosed() &&
          (readBytes = istream.read(rawbuffer)) != -1) {
        engine.decode(ByteBuffer.wrap(rawbuffer, 0, readBytes));
      }
      engine.eot();
    } catch (IOException e) {
      engine.eot();
    } catch (RuntimeException e) {
      // this catch case covers internal errors only and indicates a bug in this websocket
      // implementation
      wsl.onWebsocketError(engine, e);
      engine.closeConnection(CloseFrame.ABNORMAL_CLOSE, e.getMessage());
    }
  }
}
