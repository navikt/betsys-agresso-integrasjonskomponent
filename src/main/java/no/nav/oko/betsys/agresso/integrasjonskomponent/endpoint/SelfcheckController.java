package no.nav.oko.betsys.agresso.integrasjonskomponent.endpoint;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class SelfcheckController {

    @ResponseBody
    @RequestMapping(value = "isAlive", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> isAlive() {
        return new ResponseEntity<>("Alive", HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "isReady", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> isReady() {
        return new ResponseEntity<>("Ready", HttpStatus.OK);
    }

//    @Override
//    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        if ("/is_alive".equals(target)) {
//            handleIsAlive(response);
//            baseRequest.setHandled(true);
//        }
//
//        if ("/is_ready".equals(target)) {
//            handleIsReady(response);
//            baseRequest.setHandled(true);
//        }
//    }
//
//    private void handleIsAlive(HttpServletResponse response) throws IOException {
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        PrintWriter out = response.getWriter();
//        out.println("I'm alive!");
//    }
//
//    private void handleIsReady(HttpServletResponse response) throws IOException {
//        response.setContentType("text/html; charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        PrintWriter out = response.getWriter();
//        out.println("I'm ready!");
//    }
}

