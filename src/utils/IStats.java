package utils;

import application.Main;
import java.util.List;
import java.util.Map;

import com.sun.org.glassfish.gmbal.Description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import model.Category;
import model.DescriptionInterview;
import model.Folder;
import model.MomentExperience;
import model.Type;

public class IStats {
	
	/* Singleton */
    private IStats() {}
     
    private static IStats instance = null;
    private ArrayList<Category> mCategories = null;
    private ArrayList<DescriptionInterview> mInterviews = null;
    private ArrayList<MomentExperience> mMomentsTmp = null;
    private Map<DescriptionInterview, ArrayList<MomentExperience>> mItwMoments = null;
    
    
    
    public static IStats getInstance() {           
        if (instance == null)
        {   instance = new IStats();
        	instance.mCategories = new ArrayList<Category>();
        	instance.mInterviews = new ArrayList<DescriptionInterview>();
        	instance.mItwMoments = new HashMap<DescriptionInterview, ArrayList<MomentExperience>>();
        	instance.mMomentsTmp = new ArrayList<MomentExperience>();
        }
        return instance;
    }
    
   
    /***** REFRESHING METHODS *****/
    
    /**
     * finds the categories of the current scheme and stores it locally
     * @param t the current type of the scheme
     */
    private static void lookingForCategories(Type t) {
    	
    	for (Type t_it : t.getTypes()) { //for each type of the same level of the scheme
    					
		   	if(t_it.isCategory()) {
		   		instance.mCategories.add((Category) t_it); // if it is a category, we can store it (that is what we are looking for)
		   	}
    	
	    	else {
	    		instance.lookingForCategories(t_it); // because one category can not be parent of an other one, we stop here and we do it again for the other types
	    	}
    	}	   	
    }
    
    
    /**
     * finds the interviews of the current project and stores it locally
     * @param interviews the list of all the interviews
     */
    private static void lookingForInterviews(LinkedList <DescriptionInterview> interviews) {
    	for (DescriptionInterview interview_it : interviews) {
    		instance.mInterviews.add(interview_it);
    		instance.mItwMoments.put(interview_it, null);
		}
    }
    
    
    /**
     * finds all the moments and submoments of one specific interview and stores it locally in a temporary variable
     * @param moment the current moment.
     */
    private static void lookingForMoments(MomentExperience moment) {
    	instance.mMomentsTmp.add(moment);
    	for (MomentExperience m_it : moment.getSubMoments()) {
			instance.lookingForMoments(m_it);
		}
    }
    
  
    
    /***** STATISTICS COMPUTING *****/ 
    
    
    /**
     * refresh the singleton's attributes
     * @param main
     */
    public static void update(Main main) {
    	instance.mCategories.clear();
    	instance.mInterviews.clear();
    	instance.mItwMoments.clear();
    	instance.lookingForCategories(main.getCurrentProject().getSchema());
    	instance.lookingForInterviews(main.getCurrentProject().getInterviews());
    }
    
    /**
     * Counts the number of occurences of appearance of a specific category in a specific interview
     * @param interview the current interview
     * @param category the current category of the schema 
     * @return the number of times the category has been used in one specific interview
     */
    public static int nbOccurrences(DescriptionInterview interview, Category category) {
    	
    	int cpt = 0;
    	    	
    	/* if we are looking for a category in an interview that we have not met yet, we have to find all the moment*/
    	
    	if (instance.mItwMoments.get(interview) == null) {
    		
    		for (MomentExperience mom_it : interview.getMoments()) { // we start with the first-level moments
    			instance.lookingForMoments(mom_it); //then we find all the sub-moments employed in the model of the concerned interview
    		}
    		
    		instance.mItwMoments.put(interview, new ArrayList<MomentExperience>(instance.mMomentsTmp)); //we copy only the values of the moments found for 1 interview
    		instance.mMomentsTmp.clear(); //we clear it because we need it free for the next interview
    	
    	}
    	
    	for (MomentExperience moment : instance.mItwMoments.get(interview)) {
	    	if (moment.getTypes().contains(category)) { //then we compare if we find any category of the scheme in the model
				cpt++;
			}
    	}
    	
    	return cpt;
    }
    

    /***** GETTERS *****/ 
    
    public static ArrayList<Category> getCategories() {
    	return instance.mCategories;
    }
   
    public static ArrayList<DescriptionInterview> getInterviews() {
    	return instance.mInterviews;
    }
}

