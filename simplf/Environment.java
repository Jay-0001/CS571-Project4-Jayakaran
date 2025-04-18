package simplf; 

class Environment{
    private AssocList values;
    private final Environment enclosing;

    Environment(){
        this.values=null;
        this.enclosing=null;
    }

    Environment(Environment enclosing){
        this.values=null;
        this.enclosing=enclosing;    
    }

    Environment(AssocList assocList,Environment enclosing){
        this.values=assocList;
        this.enclosing=enclosing;
    }

    /* Return a new version of the environment that defines the variable "name"
       and sets its initial value to "value". Take care to ensure the proper aliasing
     relationship. There is an association list implementation in Assoclist.java.
     If your "define" function adds a new entry to the association list without
     modifying the previous list, this should yield the correct aliasing
     relationsip.
    
     For example, if the original environment has the association list
     [{name: "x", value: 1}, {name: "y", value: 2}]
     the new environment after calling define(..., "z", 3) should have the following
      association list:
     [{name: "z", value: 3}, {name: "x", value: 1}, {name: "y", value: 2}]
     This should be constructed by building a new class of type AssocList whose "next"
     reference is the previous AssocList.*/

    Environment define(Token varToken, String name, Object value){
        AssocList val = new AssocList(name, value, this.values);
        return new Environment(val, this.enclosing);      
    }
    
    void assign(Token name,Object value){
        for(AssocList temp=values;temp != null;temp=temp.next){
            if (temp.name.equals(name.lexeme)){
                //updation if the variable already exists
                temp.value = value;
                return;
            }
        }
        if(enclosing!=null){
            //recursively check parent environment !!
            enclosing.assign(name, value);
        }else{
            throw new RuntimeError(name, "Undefined variable '" + name.lexeme);
        }
    }

    Object get(Token name){
        for(AssocList temp=values;temp != null;temp=temp.next){
            if(temp.name.equals(name.lexeme)){
                //if matching variable found
                return temp.value;
            }
        }
        if(enclosing!=null){
            //recursively check parent environment !!
            return enclosing.get(name);
        }else{
            throw new RuntimeError(name, "Undefined variable '" + name.lexeme);
        }
    }
}

