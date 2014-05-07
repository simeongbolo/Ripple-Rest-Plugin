package sim.rest


import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * Source has been modified from the repo
 * https://github.com/grails-plugins/grails-rest-client-builder/
 *
 * deleted unused methods and changed How methods function
 */
class RequestCustomizer {

    HttpHeaders headers = new HttpHeaders()

    def body

    MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>()

    Map<String, Object> variables = [:]

    RequestCustomizer contentType(String contentType) {
        headers.setContentType(MediaType.valueOf(contentType))
        return this
    }

    RequestCustomizer body(content) {
           contentType "application/json"

            body = content
        return this
    }

    HttpEntity createEntity() {
        return mvm ? new HttpEntity(mvm, headers) : new HttpEntity(body, headers)
    }

}
