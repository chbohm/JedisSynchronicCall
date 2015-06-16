package com.axioma.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Logger {

   public static final Logger INSTANCE = new Logger();
   private Writer w;

   public static Logger getInstance() {
      return INSTANCE;
   }

   public void init(final String path) {
      try {
         this.w = new FileWriter(path);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void close() {
      try {
         this.w.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void log(final String component, final String message, final String requestId, final String status) {
      try {
         this.w.write(String.format(
                  "{\"component\":\"%s\",\"message\":\"%s\",\"requestId\":\"%s\",\"status\":\"%s\",\"timestamp\":%s}", component,
                  message, requestId, status, System.currentTimeMillis()));
         this.w.flush();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
