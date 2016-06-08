package com.avos.avoscloud.java_websocket.client;

import com.avos.avoscloud.java_websocket.WebSocketImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by lbt05 on 5/24/16.
 */
class WebSocketWriteThread implements Runnable {
  WebSocketImpl engine;
  OutputStream ostream;

  public WebSocketWriteThread(WebSocketImpl engine, OutputStream os) {
    this.engine = engine;
    this.ostream = os;
  }

  @Override
  public void run() {
    Thread.currentThread().setName("WebsocketWriteThread");
    try {
      while (!Thread.interrupted()) {
        ByteBuffer buffer = engine.outQueue.take();
        ostream.write(buffer.array(), 0, buffer.limit());
        ostream.flush();
      }
    } catch (IOException e) {
      engine.eot();
    } catch (InterruptedException e) {
      // this thread is regularly terminated via an interrupt
    }
  }
}
