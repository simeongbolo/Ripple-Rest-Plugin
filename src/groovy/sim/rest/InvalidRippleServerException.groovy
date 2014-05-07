package sim.rest
/**
 * Created by simeongbolo on 5/3/14.
 */
class InvalidRippleServerException extends RuntimeException {

    InvalidRippleServerException(String name , Object clazz){
        super("Cannot connect to the ripple server got exception \n"+name)

    }
}
