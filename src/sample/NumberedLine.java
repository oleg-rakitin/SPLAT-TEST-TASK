package sample;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class NumberedLine {
    final int number;
    final String line;
    final Path path;
    Map<String,Path> hm = new HashMap<String,Path>();
    NumberedLine(int number, String line, Path path) {
        this.number = number;
        this.line = line;
        this.path = path;
        setMap(line,path);
    }

    private void setMap(String line, Path path) {
    }

    public int getNumber() {
        return number;
    }
    public String getLine() {
        return line;
    }

    public Path getPath(){
        return path;
    }

    public void setMap(){
        hm.put(line,path);
    }


    @Override
    public String toString() {
        return path + ":\t" + number+":\t"+line;
    }

}
