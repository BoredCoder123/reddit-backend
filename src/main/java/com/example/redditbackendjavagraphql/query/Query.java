package com.example.redditbackendjavagraphql.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component
public class Query implements GraphQLQueryResolver {
    public String heartbeat(){
        return new Date(System.currentTimeMillis()).toString();
    }
}
