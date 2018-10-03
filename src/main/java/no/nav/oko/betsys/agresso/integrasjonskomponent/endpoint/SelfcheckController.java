package no.nav.oko.betsys.agresso.integrasjonskomponent.endpoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        //TODO: Add some selftests
        return new ResponseEntity<>("Ready", HttpStatus.OK);
    }
}

