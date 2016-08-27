
class ActPhys implements Runnable{
    Being being;
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
    public void run(){
        if(version == 0)
            being.act();
        if(version == 1)
            being.updateXY();
    }
}