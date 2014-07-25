package edu.jhu.cvrg.ceptools.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.kernel.util.PropsUtil;

import edu.jhu.cvrg.ceptools.main.ViewPublications;
import edu.jhu.cvrg.ceptools.model.FileStorer;




@ManagedBean(name="fileUploadController")
@ViewScoped

public class FileUploadController implements Serializable{

	private UploadedFile file;
    private ArrayList<UploadedFile> newfiles;

	private static final long serialVersionUID = 2L;
    
    private int pmid;
    private static Logger logger = Logger.getLogger(ViewPublications.class.getName());
    private ArrayList<FileStorer> newrefinedfiles;
    private ArrayList<FileStorer> filesondrive;
    
    public FileUploadController()
    {
    	newfiles = new ArrayList<UploadedFile> ();
    	pmid = 0;
    	newrefinedfiles = new ArrayList<FileStorer> ();
    	filesondrive = new ArrayList<FileStorer> ();

    }
    
    public void setFilesondrive(ArrayList<FileStorer> f)
    {
    	filesondrive = f;
    }
    
    public ArrayList<FileStorer> getFilesondrive()
    {
    	return filesondrive;
    }
    
    public ArrayList<FileStorer> getNewrefinedfiles ()
    {
    	return newrefinedfiles;
    }
    
    public void setNewrefinedfiles(ArrayList<FileStorer> f)
    {
    	
    	newrefinedfiles = f;
    }

    public UploadedFile getFile() {
        return file;
    }
    
    public int getPmid()
    {
    	return pmid;
    }
    
    public void setPmid(int pmid)
    {
    	this.pmid = pmid;
    }
    
    public void setNewfiles(ArrayList<UploadedFile> newfiles)
    {
    	this.newfiles = newfiles;
    }
    
    public ArrayList<UploadedFile> getNewfiles()
    {
    	return newfiles;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void upload() {
        FacesMessage msg = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public boolean checkUploads()
    {
    	
    	boolean filecheck = false;

    	for(FileStorer currfile: filesondrive)
    	{
    		if(currfile.getFilename().equals(file.getFileName()))
    		{
    			filecheck = true;
    		}
    	}
        	
    	for (FileStorer currufile :newrefinedfiles)
    	{
    		if(currufile.getFilename().equals(file.getFileName()))
    		{
    			filecheck = true;
    		}
    	}
    	
    	if(filecheck == true)
    	{
    
    		return false;
    	}
    	else
    	{
    		return true; 		
    	}
    }
    
    public void RetrieveFiles() {
		 
		
	
    	String currlocation = PropsUtil.get("data_store2") + this.pmid + "/";
    	File folder = new File(currlocation);

    	for(File currfile: folder.listFiles())
    	{
    		
           String absolutePath = currfile.getAbsolutePath();
    		FileStorer currfilestore = new FileStorer();
    		currfilestore.setFilename(currfile.getName());
    		currfilestore.setFilelocation(currlocation);
    		currfilestore.setFiletype(FilenameUtils.getExtension(currfile.getName()));
    		currfilestore.setLocalfilestore( absolutePath.substring(0,absolutePath.lastIndexOf(File.separator)));
    		
    		filesondrive.add(currfilestore);
    	}
    	
    	
    }
    
    public void FileSave()
    {
    	
    	String currpmid  = String.valueOf(pmid);
    	String currlocation = PropsUtil.get("data_store2")+ currpmid+"/";

    	
    	FileStorer currfstore = new FileStorer();
    	File thedir = new File(currlocation);
    	
    	 
    	if(!thedir.exists())
    	{
 
    	 thedir.mkdirs();
   
    	}

    	//Now add files to it
    	 try
    	   {
    		
    	 
    		 for(org.primefaces.model.UploadedFile currfile: newfiles)
    		 { 
	    		currfstore = new FileStorer();
	    		
	    		currfstore.setFilename(currfile.getFileName());
	    		currfstore.setFilesize(currfile.getSize());
		    	currfstore.setFilelocation(currlocation);
		    	currfstore.setFiletype(FilenameUtils.getExtension(currfile.getFileName()));
		    		
	
		    	String fullfilelocation = currlocation + currfile.getFileName();
		    	File myfile = new File(fullfilelocation);
		    	
		    	newrefinedfiles.add(currfstore);
	    	   
		    	OutputStream out = new FileOutputStream(myfile);
		    	InputStream in = currfile.getInputstream();
	
		    	org.apache.commons.io.IOUtils.copy(in, out);
	
	    		out.close();
    		 }
    	
    	}
    	catch(Exception ex)
    	{
    		logger.info(ex);
    	}
    	
    	
    }
    


    public void handleFileUpload(FileUploadEvent event) {
    	if(event!=null)
    	{
    	
    	newfiles = new ArrayList<UploadedFile> ();
    	
    	this.setFile(event.getFile());
    	
    	if(filesondrive.isEmpty())
    	{
    		 RetrieveFiles();	
    	}
    	
    	if(checkUploads())
    	{
	    	this.newfiles.add(event.getFile());
	    	FileSave();
	    	FacesMessage msg = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
    	}
    	else
    	{
    		FacesMessage msg = new FacesMessage("You can not upload a file with the same name as a file already stored.", file.getFileName() + " was not saved. Please change the name and try again.");
    	    FacesContext.getCurrentInstance().addMessage(null, msg);
    	}
    	
    	
	
    	}
   
	}
    
   
    
  
    
    
}