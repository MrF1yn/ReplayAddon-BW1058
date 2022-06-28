package dev.mrflyn.replayaddon.advancedreplayhook;

import java.util.List;

public class StartQueue {

    public Integer startTime = null;
    public List<Double> location = null;

    public StartQueue(Integer startTime, List<Double> location){
        this.location = location;
        this.startTime = startTime;
    }


}
