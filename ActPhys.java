
class ActPhys implements Runnable{
    Being being;
    Being[] beings;
    int version;
    /**
     * Make a new ActPhys Runnable of type act or updateXY
     * v(0) = act();
     * v(1) = updateXY();
     */
    public ActPhys(Being b, int v){
        being = b;
        assert v==0 || v==1;
        version = v;
        String to = "";
        if(v == 0)
            to = "to act";
        if(v == 1)
            to = "to move";
        Screen.debugShout(b+"\tAdd: "+to, 3);
    }
    public ActPhys(Being[] b, int v){
        beings = b;
        assert v==0 || v==1;
        version = v;
        String to = "";
        if(v == 0)
            to = "to act";
        if(v == 1)
            to = "to move";
        Screen.debugShout(b+"\tAdd "+beings.length+" Phys "+to, 3);
    }
    public void run(){
        if( beings == null ){
            if(version == 0)
                being.act();
            else if(version == 1)
                being.updateXY();
        }
        else{
            if(version == 0)
                for(Being b : beings)
                    b.act();
            else if(version == 1)
                for(Being b : beings)
                    b.updateXY();
        }
    }
}