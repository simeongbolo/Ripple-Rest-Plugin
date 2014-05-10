package sim.entity

import grails.web.JSONBuilder

/**
 * Created by simeongbolo on 5/5/14.
 * This is a utility for setting object properties that will be
 * used by the RippleRestClientService for submitting requests
 * and returning full domain objects from requests
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


    // your API, provide a Map of changes to update a entity. the map value may be static value, or a closure that take up to 2 params
    def update( entity, Map changes ){
        changes?.each {k, v ->
            def newValue;
            if (v instanceof Closure){
                switch (v.parameterTypes.length) {
                    case 0: newValue = v(); break;
                    case 1: newValue = v(entity[k]); break; // if one params, the closure is called with the field value
                    case 2: newValue = v(entity[k],entity); break; // if two params, the closure is called with teh field value and the entity
                }
            }else{
                newValue = v
            }
            entity[k] = newValue
        }
    }

}
