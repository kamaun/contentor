package group13.studybuddy;

import java.util.ArrayList;
import java.util.List;

final class DropDownList {
    private List<String> viewerList;
    private ArrayList<Integer> viewerID;

    public DropDownList(List<String> vList, ArrayList<Integer> vID){
        this.viewerList = vList;
        this.viewerID = vID;
    }

    public ArrayList<Integer> getViewerID() {
        return viewerID;
    }

    public List<String> getViewerList() {
        return viewerList;
    }
}
