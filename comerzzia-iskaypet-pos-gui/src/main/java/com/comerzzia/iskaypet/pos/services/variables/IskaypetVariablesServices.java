package com.comerzzia.iskaypet.pos.services.variables;

import com.comerzzia.pos.services.core.variables.VariablesServices;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class IskaypetVariablesServices extends VariablesServices {

    protected static IskaypetVariablesServices instance;

    public static IskaypetVariablesServices get(){
        if(instance == null){
            instance = new IskaypetVariablesServices();
        }
        return instance;
    }

}