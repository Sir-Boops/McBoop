package me.boops.functions;

public class GetCLIArg {
    
    public String get(String[] args, String term) {
        String ans = "";
        for(int i = 0; i < args.length; i++) {
            if(args[i].equalsIgnoreCase(term)) {
                ans = args[i + 1];
            }
        }
        return ans;
    }
}
