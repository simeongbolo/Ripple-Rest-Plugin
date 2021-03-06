package sim.rest

import grails.converters.JSON
import grails.converters.XML
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement
import org.springframework.web.client.HttpStatusCodeException

/**
 * This isource is from  https://github.com/grails-plugins/grails-rest-client-builder/
 */
class ErrorResponse {

    def message

    @Delegate HttpStatusCodeException error

    @Lazy String text = {
        error.responseBodyAsString
    }()

    @Lazy JSONElement json = {
        def body = error.responseBodyAsString
        if (body) {
            return JSON.parse(body)
        }
    }()

    @Lazy GPathResult xml = {
        def body = error.responseBodyAsString
        if (body) {
            return XML.parse(body)
        }
    }()

    byte[] getBody() {
        error.responseBodyAsByteArray
    }

    int getStatus() {
        error.statusCode?.value() ?: 200
    }
}
