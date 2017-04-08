/**
 * A modified LinkedList that allows for a sublist (A-list)
 * In this case, the subList is for those particles that act.
 */
public class BiList<E>{// implements java.util.List{
    Node o1 = null;
    Node a1 = null;
    public BiList(){
        
    }
    //Total size
    public int size(){
        int s = 0;
        if(o1 != null){
            s++;
            Node curr = o1;
            while(curr.getNext()!= null){
                s++;
                curr = curr.getNext();
            }
        }
        return s;
    }
    //Size of A-sublist
    public int sizeA(){
        int s = 0;
        if(a1!= null){
            s++;
            Node curr = a1;
            while(curr.getNextA()!= null){
                s++;
                curr = curr.getNextA();
            }
        }
        return s;
    }
    /**
     * Add a being to the List
     */
    public void add(E o){
        Node newNode = new Node(o);
        newNode.setNext(o1);
        o1 = newNode;
    }
    /**
     * Add a being to the A-List
     */
    public void addA(E a){
        Node newNode = new Node(a);
        newNode.setNext(o1);
        o1 = newNode;
        newNode.setNextA(a1);
        a1 = newNode;
    }
    /**
     * Remove a being. Returns true if the being was found.
     */
    public boolean remove(E r){
        if(o1.getVal() == r){
            //If this is not true, o1 is not an a, and the A list is not affected
            if(a1 == o1)
                a1 = o1.getNextA();
            o1 = o1.getNext();
            return true;
        }
        Node curr = o1;
        while(curr.hasNext()){
            Node next = curr.getNext();
            if(next.getVal() == r){
                //Remove next
                curr.setNext(next.getNext());
                /* Remove next from A list (if it's there)
                 */
                if(a1 != null){
                    if(a1 == next){
                        a1 = a1.getNextA();
                        return true;
                    }
                    //I use the same search variable because it doesn't matter
                    curr = a1;
                    Screen.debugShout("Curr= "+curr, 3);
                    Screen.debugShout("Curr.hasNextA= "+curr.hasNextA(), 3);
                    while(curr.hasNextA()){
                        if(curr.getNextA() == next){
                            curr.setNextA(next.getNextA());
                            return true;
                        }
                        curr = curr.getNextA();
                        Screen.debugShout("Curr= "+curr, 3);
                        Screen.debugShout("Curr.hasNextA= "+curr.hasNextA(), 3);
                    }
                }
                return true;
            }
            curr = next;
        }
        return false;
    }
    public boolean contains(E o){
        Node curr = o1;
        if(curr.getVal() == o)
            return true;
        while(curr.hasNext()){
            if(curr.getVal() == o)
                return true;
            curr = curr.getNext();
        }
        return false;
    }
    public class Node{
        private E val;
        private Node next;
        private Node nextA;
        
        public Node(E o){
            val = o;
        }
        public E getVal(){
            return val;
        }
        public Node getNext(){
            return next;
        }
        public Node getNextA(){
            return nextA;
            //OLD: return a1 if null
        }
        public void setNext(Node n){
            next = n;
        }
        public void setNextA(Node n){
            nextA = n;
        }
        public boolean hasNext(){
            if(next != null && next.getVal()!= null)
                return true;
            return false;
        }
        public boolean hasNextA(){
            if(nextA != null && nextA.getVal()!= null)
                return true;
            return false;
        }
    }
}
