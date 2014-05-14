package sim.rest

/**
 * Created by simeongbolo on 5/13/14.
 */
class RippleRequestException extends RuntimeException {

    RippleRequestException(String message , Object clazz){
        super("There was an error processing your request: "+message)

    }

}
