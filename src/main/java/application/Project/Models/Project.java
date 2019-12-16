package application.Project.Models;

import Persistency.ProjectSaver;
import Components.SchemaTree.Cell.Models.SchemaTreeRoot;
import interviewSelector.Models.Interview;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.Serializable;

public class Project implements Serializable {

    private SimpleStringProperty name;
    private SimpleObjectProperty<SchemaTreeRoot> schemaTreeRoot;

    private SimpleListProperty<Interview> interviews;
    private ReadOnlyListWrapper<Interview> readOnlyInterviews;

    private SimpleObjectProperty<Interview> selectedInterview;

    public Project(String name, SchemaTreeRoot baseScheme) {
        this.name = new SimpleStringProperty(name);
        this.schemaTreeRoot = new SimpleObjectProperty<SchemaTreeRoot>(baseScheme);

        this.interviews = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.readOnlyInterviews = new ReadOnlyListWrapper<>(this.interviews);

        this.selectedInterview = new SimpleObjectProperty<>();
    }

    public String getName() { return this.name.get(); };
    public StringProperty nameProperty() { return this.name; }

    public SchemaTreeRoot getSchemaTreeRoot() { return schemaTreeRoot.get(); };
    public SimpleObjectProperty<SchemaTreeRoot> schemaTreeRootProperty() {return schemaTreeRoot;};

    public void addInterview(Interview i) {
        interviews.add(i);
        setSelectedInterview(i);
    }
    public void removeInterview(Interview i) {
        interviews.remove(i);
        if(i == selectedInterview.get())
            setSelectedInterview(interviews.size() > 0 ? interviews.get(0) : null);
    }
    public ReadOnlyListWrapper<Interview> interviewsProperty() { return readOnlyInterviews; }

    public Interview getSelectedInterview() { return selectedInterview.get(); }
    public void setSelectedInterview(Interview interview) {
        System.out.println(interview);
        if(interviews.contains(interview) || interview == null){
            selectedInterview.set(interview);
        }
        else
            throw new IllegalArgumentException("The selected interview is not contained in the current project !");
    }
    public ObservableValue<Interview> selectedInterviewProperty() { return selectedInterview; }

    public void saveAs(String name, String path) throws IOException {
        if(!name.contains(".upmt"))
            name += ".upmt";
        ProjectSaver.save(this, path+name);
    }

}
