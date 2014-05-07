package sim.entity

import grails.web.JSONBuilder

/**
 * Created by simeongbolo on 5/5/14.
 * This is a utility for setting object properties that will be
 * used by the RippleRestClientService for submitting requests
 */
class EntityCreationDelegate {

    def root
    def current
    def obj

    def createEntity(Object clazz, Closure c){
        c.delegate = this
        obj = clazz;
        current = [:]
        def returnValue = c.call()
    }



    void setProperty(String propName, Object value) {
            obj.setProperty(propName,value)
            current[propName] = value



    }

    def deNull(obj) {
        if(obj instanceof Map) {
            obj.collectEntries {k, v ->
                if(v) [(k): denull(v)] else [:]
            }
        } else if(obj instanceof List) {
            obj.collect { denull(it) }.findAll { it != null }
        } else {
            obj
        }
    }

    def deEmpty(obj) {
        if(obj instanceof Map) {
            obj.collectEntries {k, v ->
                if(!(v instanceof  ConfigObject)) [(k): deEmpty(v)] else [:]
            }
        } else if(obj instanceof List) {
            obj.collect { deEmpty(it) }.findAll { it != null }
        } else {
            obj
        }
    }

}
