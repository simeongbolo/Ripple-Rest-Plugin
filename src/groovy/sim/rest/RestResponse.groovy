package sim.rest

import grails.converters.JSON
import grails.converters.XML
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.springframework.http.ResponseEntity

/**
 * Modified from
 * https://github.com/grails-plugins/grails-rest-client-builder/
 * used for processing a rest response
 */
class RestResponse {

    @Delegate ResponseEntity responseEntity

    @Lazy JSONElement json = {
        def body = responseEntity.body
        if (body) {
            return JSON.parse(body)
        }
    }()

    @Lazy GPathResult xml = {
        def body = responseEntity.body
        if (body) {
            return XML.parse(body)
        }
    }()

    @Lazy String text = {
        def body = responseEntity.body
        if (body) {
            return body.toString()
        }

        responseEntity.statusCode.reasonPhrase
    }()

    int getStatus() {
        responseEntity?.statusCode?.value() ?: 200
    }
}
