package edu.jhu.cvrg.ceptools.main;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import edu.jhu.cvrg.ceptools.controller.FileDownloadController;
import edu.jhu.cvrg.ceptools.controller.FileUploadController;
import edu.jhu.cvrg.ceptools.model.FileStorer;
import edu.jhu.cvrg.ceptools.model.Publication;
import edu.jhu.cvrg.ceptools.utility.ZipDirectory;

@ManagedBean(name="viewPublications")
@SessionScoped

public class ViewPublications implements Serializable{

	private int step;
	
	private static final long serialVersionUID = 3L;
	private List<Publication> publications;
	private List<Publication> results;
	private List<Publication> checklist;
	 
    private String pSearcher;
    private String searcher;
    
    public int counter;
    private String buttonvalue;
    private  String userauthor;  
    private String usertitle;
    private String userpmid; 
    private String userId;
  
    private Publication selectedPub;
    private FileUploadController fchooser;
    private FileDownloadController fdownload;
    private FileStorer selecteddownloadfile;
    private Publication selectedpub;
    
    private String selecteddownloadfiletype;
    private String selecteddownloadfilename;
    private String pubselecterr;
    private HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr");
	private SolrInputDocument metadoc = new SolrInputDocument();
    
    private boolean confirmed;
	List<String> filesanddata = new ArrayList<String>();
    private List<File> files;
    private List<String> filenames;
    private List<FileStorer> allfiles;
    private List<FileStorer> newfiles;
    private List<FileStorer> filesfromsolr;
    private FileStorer selectedfile;
 
    private ZipDirectory zip;
    private int index;
    private String pmid;
    int solrindex;
 
    private String searchchoice;

    private static Logger logger = Logger.getLogger(ViewPublications.class.getName());
    
	public ViewPublications()
	{
	
		 step = 1;
    	 counter = 0;
    	 searcher = "title";
    	 buttonvalue = "";
    	 userauthor = usertitle = userpmid  = searcher = pSearcher = "";
    	
    	 publications = new ArrayList<Publication>();
    	 results = new ArrayList<Publication>();
    	 checklist = new ArrayList<Publication>();		 
    	
    	 selectedPub = new Publication();
    	 fchooser = new FileUploadController();
    	 files = new ArrayList<File>();
    	 filenames = new ArrayList<String>();
    	 allfiles = new ArrayList<FileStorer>();
    	 selectedfile = null;
    	 zip = new ZipDirectory();
    	 fdownload = new FileDownloadController();
    	 selecteddownloadfile = null;
    	 index = 0;
    	 selectedpub = new Publication();
    	
    	 filesfromsolr = new ArrayList<FileStorer> ();
    	 userId = Long.toString(LiferayFacesContext.getInstance().getUser().getUserId());
    	 
    	 getUserEntries();
	}
	
	
	
	public void Clean()
	{
		 step = 1;
	 	 counter = 0;
	 	 searcher = "title";
	 	 buttonvalue = "";
	 	 userauthor = usertitle = userpmid  = searcher = pSearcher = "";
	 
	 	 publications = new ArrayList<Publication>();
	 	 results = new ArrayList<Publication>();
	 	 checklist = new ArrayList<Publication>();		 

	 	 selectedPub = new Publication();
	 	 fchooser = new FileUploadController();
	 	 files = new ArrayList<File>();
	 	 filenames = new ArrayList<String>();
	 	 allfiles = new ArrayList<FileStorer>();
	 	 newfiles = new ArrayList<FileStorer>();
	 	 selectedfile = null;
	 	 zip = new ZipDirectory();
	 	 fdownload = new FileDownloadController();
	 	 selecteddownloadfile = null;
	 	 index = 0;
	 	 filesanddata = new ArrayList<String> ();
	 	 selecteddownloadfiletype = selecteddownloadfilename = "";
	  	 searchchoice = "searchtitle";  

	  	 confirmed = false;
		
		 selectedpub = new Publication();
    
    	 filesfromsolr = new ArrayList<FileStorer> ();
    	 
    	 getUserEntries();
	}
	
	public String getUserId()
	{
		return userId;
	}
	
	public void setUserId(String m)
	{
		userId = m;
	}

	
	 	public Publication getSelectedpub()
	    {
	    	return selectedpub;
	    }
	    
	    public void setSelectedpub(Publication spub)
	    {
	    	this.selectedpub = spub;
	    }
	 
	  	public List<Publication> getResults()
	    {
	    	return results;
	    }
	    
	    public void setResults(List<Publication> re)
	    {
	    	results = re;
	    }
	    
	    public List<Publication> getChecklist()
	    {
	    	return checklist;
	    }
	    
	    public void setChecklist(List<Publication> re)
	    {
	    	checklist = re;
	    }
	    
	    public String getPubselecterr()
	    {
	    	return pubselecterr;
	    }
	    
	    public void setPubselecterr(String e)
	    {
	    	
	    	pubselecterr = e;
	    }
	    
	    public int getIndex()
	    {
      	return index;
	    }
      
      public void setIndex(int i)
      {
      	this.index = i;
      }
      
      public List<FileStorer> getNewfiles()
	    {
	    	return newfiles;
	    }
	    
	    public void setNewfiles(List<FileStorer> newfiles)
	    {
	    	this.newfiles = newfiles;
	    }
      
	    public List<FileStorer> getAllfiles()
	    {
	    	return allfiles;
	    }
	    
	    public void setAllfiles(List<FileStorer> allfiles)
	    {
	    	this.allfiles = allfiles;
	    }
	    
	    public Publication getSelectedPub()
	    {
	    	return selectedPub;
	    }
	    
	    public void setSelectedPub(Publication spub)
	    {
	    	this.selectedPub = spub;
	    }
	    
	    public List<Publication> getPublications()
	    {
	    	return publications;
	    }
	    
	    public void setPublications(List<Publication> publist)
	    {
	    	this.publications = publist;
	    }
	    
	    
	    public  String getUserauthor()
      {
      	return userauthor;
      }
      
      public  String getUsertitle()
      {
      	return usertitle;
      }
      public  String getUserpmid()
      {
      	return userpmid;
      }
      
      public  void setUserauthor(String u)
      {
      	userauthor = u;
      }
      
      public  void setUsertitle(String u)
      {
      	usertitle = u;
      }
      
      public  void setUserpmid(String u)
      {
      	userpmid = u;
      }
	   
      public String setButtonvalue ()
      {
      	return buttonvalue;
      }
      
      public void getButtonvalue(String b)
      {
      	this.buttonvalue = b;
      }
      
      public void setStep(int currstep)
	   {
		   step = currstep;
	   }
	   
	   public int getStep()
	   {
		   return step;
	   }
	   
	   
	   public void setSearchchoice(String currsearch)
	   {
		   searchchoice = currsearch;
		   
	   }
	
	   public String getSearchchoice()
	   {
		   return searchchoice;
	   }
	   
	  public void setFilestring()
	 {
	 	for(File currfile:this.files)
	 	{
	 		filenames.add(currfile.getName());
	 	}
	 }


	 public void setFilenames(List<String> filenames)
	 {
	 	  this.filenames = filenames;
	 }

	 public List<String> getFilenames()
	 {
	 	  return filenames;
	 }
	   
	  public void setSelecteddownloadfile(FileStorer thefile)
	   {
		   this.selecteddownloadfile = thefile;
	   }
	   
	   public FileStorer getSelecteddownloadfile()
	   {
		   return selecteddownloadfile;
	   }
	   
	   public void setFdownload(FileDownloadController fdownload)
	   {
		   this.fdownload = fdownload;
	   }
	   
	   
	   public FileDownloadController getFdownload()
	   {
		   return fdownload;
	   }
	   
	   public void setSelectedfile(FileStorer thefile)
	   {
		   selectedfile = thefile;
	   }
	   
	   public FileStorer getSelectedfile()
	   {
		   return selectedfile;
	   }
	   
	   public FileUploadController getFchooser()
	    {
	    	return fchooser;
	    }
	    
	    public void setFchooser( FileUploadController fchooser)
	    {
	    	this.fchooser =  fchooser;
	    }
	   
	      
	    public String editPSearcher(String pSearcher)
	    {
	    	this.pSearcher = pSearcher;
	    	return pSearcher;
	    }
	    
	    public void setPSearcher(String pSearcher)
	    {
	    	this.pSearcher = pSearcher;
	    }
	 
	    public String getPSearcher()
	    {
	    	return pSearcher;
	    }
	    
	    public String getSearcher()
	    {
	    	return searcher;
	    }
 
	    public void setSearcher(String searcher)
	    {
	    	this.searcher = searcher;
	    }
	    
	    public int getSolrindex()
		{
			return solrindex;
		}
		
		public void setSolrIndex(int s)
		{
			solrindex = s;
		}
		
		public void setFilesfromsolr(List<FileStorer> f)
		{
			filesfromsolr = f;
		}
		
		public List<FileStorer> getFilesfromsolr()
		{
			return filesfromsolr;
		}
		
		public void handleFileSave(FileUploadEvent event)
		{
			if(event!=null)
	    	{
				fchooser.setNewfiles( new ArrayList<UploadedFile> ());
		    	
		    	fchooser.setFile(event.getFile());
		    	
		    	if(fchooser.getFilesondrive().isEmpty())
		    	{
		    		 fchooser.RetrieveFiles();	
		    	}
		    	
		    	if(fchooser.checkUploads())
		    	{
		    		fchooser.getNewfiles().add(event.getFile());
			    	fchooser.FileSave();
			    	draftPointSave1();
			    	FacesMessage msg = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
		    	}
		    	else
		    	{
		    		FacesMessage msg = new FacesMessage("You can not upload a file with the same name as a file already stored.", fchooser.getFile().getFileName() + " was not saved. Please change the name and try again.");
		    	    FacesContext.getCurrentInstance().addMessage(null, msg);
		    	}
		    	
		    	
		    	
		    	
				
	    	}
		}
	
	    
	    public void handleRefresh1(ActionEvent event)
	    {
	    	if(event!=null)
	    	{
	    		FacesMessage msg1 = new FacesMessage("Refreshing data...please wait.", "");
	    		FacesContext.getCurrentInstance().addMessage(null, msg1);
	    		
	    		refreshData();
	    		
	    		FacesMessage msg2 = new FacesMessage("Data Refreshed.", "");
	    		FacesContext.getCurrentInstance().addMessage(null,msg2);
	    	}
	    	
	    }
	    
	    public void refreshData()
	    {
	    	publications = new ArrayList<Publication>();
	    	getUserEntries();
	    	
	    }
	
	  public void moveStep(int nextstep)
   {
		 if(nextstep > 5)
		 {
			 step = nextstep;
		 }

		  switch(step)
		  {
		  
		  case 1:
			  if(selectedpub != null)
			   {
				   step = 2;
				   fchooser.setPmid(selectedpub.getPmid());
				   RetrieveFiles();
				   configDisplay();
				   pubselecterr = "";
			   }
			   else
			   {
				   pubselecterr = "You must select a publication before moving forward.";
			   }
			  break;
		  case 2:
			  step = 3;
			  break;
		  case 3:
			  validateDesc();
				
			   
			   if(confirmed==true)
			   {
				  
				   combineFiles();

				   zipTheFolder();
				   configDisplay ();
				   draftPointSave3();
				   
				   step = 4;  
				   
			   }
			   else
			   {
				   draftPointSave2();
				   step = 3;
			   }
			  break;
		  case 4: 
	      case 5:	  
			  Clean();
			  step = 1;
			  break;
			  
		  case 6:
			
				  combineFiles();
			  draftPointSave1();
			
			  Clean();
			  logger.info("here2");
			  step = 1;
			  break;
		  case 7:
			
			  combineFiles();
			  draftPointSave2();
			
			  
			  Clean();
			  logger.info("here2");
			  step = 1;
			  break;
			  
			  
		  default: step = 1;
          break;
		  }


			   try{
					LiferayFacesContext portletFacesContext = LiferayFacesContext.getInstance();
					portletFacesContext.getExternalContext().redirect("edit");
					return;
				}
				catch (Exception ex)
				{
					logger.info(ex);
				}
			}
	  
	  public void configDisplay ()
	   {
		 
		   String tmpdis = "";
		   
		   for(FileStorer currfile: selectedpub.getPubfiles())
		   {
			  
			   if(currfile.getFigure().length() > 0 && currfile.getPanel().length()>0)
			   {
				   tmpdis = "Figure " + currfile.getFigure() + ", Panel " + currfile.getPanel();
				   currfile.setFigpandisplay(tmpdis); 
			   }
			   else if (currfile.getFigure().length() > 0 && currfile.getPanel().length()<1)
			   {
				   tmpdis = "Figure " + currfile.getFigure(); 
				   currfile.setFigpandisplay(tmpdis);  
			   }
			   else 
			   {
				   currfile.setFigpandisplay("");  
			   }
			   
			   
		   }
		   
		   
		   
	   }

	  
	  public void zipTheFolder()
	  {
		  String currlocation = PropsUtil.get("data_store2") + this.selectedpub.getPmid() + "/";
		  String zipfilelocation = currlocation+ this.selectedpub.getPmid()+".zip";
	  
	    	try{
	    		
	    		zip.setPmid(this.selectedpub.getPmid());
		    	zip.Zipfiles(currlocation, zipfilelocation); 
		    	
	    
		    	}
		    	catch (Exception ex)
		    	{
		    		logger.info(ex);
		    		
		    	}
	  }
	  
	  
	  
	  public void validateDesc()
	    {

	       boolean verify = true;

		   for(FileStorer currfile: selectedpub.getPubfiles())
		   { 
			
			   //Validate Figure

			   //Validate Panel
			   if(currfile.getFigure().length() < 1 && currfile.getPanel().length()>=1)
			   {
				currfile.setMessage("You must enter a figure number to correspond to the panel entered.");
				verify= false;
			   }
			   
			   
			   //Validate Description
			   
			   else if(currfile.getFigure().length() < 1 && currfile.getPanel().length()<1 && currfile.getDescription().length()<1)
			   {
				   currfile.setMessage("You must enter a description if there is no Figure or Panel number entered.");
				   verify= false;
			   }
			   else
			   {
				   currfile.setMessage("");
			   }
		

	      }

		   for(FileStorer currfile2: fchooser.getNewrefinedfiles())
		   { 

			   //Validate Panel
			   if(currfile2.getFigure().length() < 1 && currfile2.getPanel().length()>=1)
			   {
				   
				   currfile2.setMessage("You must enter a figure number to correspond to the panel entered.");
				   
				  verify= false;
			   }
			   
			   
			   //Validate Description
			   
			   else if(currfile2.getFigure().length() < 1 && currfile2.getPanel().length()<1 && currfile2.getDescription().length()<1)
			   {
				  
				   currfile2.setMessage("You must enter a description if there is no Figure or Panel number entered.");
				   
				   verify= false;
			   }
			   
			   else
			   {

				   currfile2.setMessage("");
			   }
		
			     
			   
	      }
		   
		   if(verify == true)
		   {
			   confirmed = true;
		   }
		   

	    }
	  
	  
	  public void downloadRawFiles(FileStorer currfile){

			selecteddownloadfile = currfile;

		    if(selecteddownloadfile != null){
		    	
		           downloadInit();
		    }
		
		}
		
		public void downloadZipOnly()
		{
			
			
			selecteddownloadfile = new FileStorer();
			
			String fileloc = PropsUtil.get("data_store2") + pmid + "/";
			String filen = pmid + ".zip";
			
			selecteddownloadfile.setFilelocation(fileloc);
			selecteddownloadfile.setFilename(filen);
			selecteddownloadfile.setIndex(0);
			selecteddownloadfile.setLocalfilestore(fileloc);
			selecteddownloadfile.setFiletype("zip");
			
			
			downloadFile(filen,"zip");
		}
		
		public void downloadInit()
		{
			
			
			//Gather the content type and store
			selecteddownloadfilename = selecteddownloadfile.getFilename();

			String currtype = FilenameUtils.getExtension(selecteddownloadfile.getFilename());
			selecteddownloadfiletype = currtype;

		    selecteddownloadfile.setFiletype(selecteddownloadfiletype);
			//Begin the download process
			downloadFile(selecteddownloadfilename,  selecteddownloadfile.getFiletype());
			
		
		}
		
		
public void downloadFile(String filename, String filetype){
	         
	    //Approved file types
		//doc|docx|xls|xlsx|pdf|abf|xml|pgf|pul|amp|dat|txt|zip|tar
	         
	         String contentType = "";
	 
	         if(filetype.equals("zip") ){
	             contentType = "application/zip";
	          }
	         else if(filetype.equals("tar") ){
	             contentType = "application/tar";
	          }
	         else if(filetype.equals("xls") || filetype.equals("xlsx")){
	              contentType = "application/xls";
	         }
	         
	         else if(filetype.equals("doc") || filetype.equals("docx")){
	             contentType = "application/doc";
	          }
	         
	         else if(filetype.equals("pdf") ){
	             contentType = "application/pdf";
	          }
	         
	         else if(filetype.equals("xml") ){
	             contentType = "text/xml";
	          }
	         else
	         {
	        	 contentType = "text/plain";
	         }
	       
	    
	         
	  FacesContext facesContext = (FacesContext) FacesContext.getCurrentInstance();
	  ExternalContext externalContext = facesContext.getExternalContext();
	  PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
	  HttpServletResponse response = PortalUtil.getHttpServletResponse(portletResponse);


	  File file = new File(selecteddownloadfile.getFilelocation(), filename);
	  BufferedInputStream input = null;
	  BufferedOutputStream output = null;

	 
	  try {
	  input = new BufferedInputStream(new FileInputStream(file), 10240);
	 
	  response.reset();
	  response.setHeader("Content-Type", contentType);
	  response.setHeader("Content-Length", String.valueOf(file.length()));
	  response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	  response.flushBuffer();
	  output = new BufferedOutputStream(response.getOutputStream(), 10240);
	  
	  byte[] buffer = new byte[10240];
	  int length;
	  while ((length = input.read(buffer)) > 0) {
	  output.write(buffer, 0, length);
	  }
	 
	  output.flush();
	  } catch (FileNotFoundException e) {
	                 e.printStackTrace();
	         } catch (IOException e) {
	                 e.printStackTrace();
	         } finally {
	  try {
	                         output.close();
	  input.close();
	                 } catch (IOException e) {
	                         e.printStackTrace();
	                 }
	  }
	 
	  facesContext.responseComplete();

	 
	}
	  
	  public void setPubforEdit(Publication currpub)
	  {
		  selectedpub = currpub;
		  moveStep(2);
	  }

public void handleFileSavePoint1(ActionEvent event) {
	if(event!=null)
	{
	
	FacesMessage msg1 = new FacesMessage("Saving your progress...please wait.", "");
	FacesContext.getCurrentInstance().addMessage(null, msg1);
	
	if(step ==2)
	{
	draftPointSave1();
	}
	else
	{
	draftPointSave2();
	}

	
	FacesMessage msg2 = new FacesMessage("Your progress is saved."," You may access your draft under 'View My Publications'.");
	FacesContext.getCurrentInstance().addMessage(null, msg2);
	}
}

public void setSOLRVariables()
{

	
	 filesanddata = new ArrayList<String> ();
	String allstrings = "";

	for(FileStorer currfile: allfiles)
	{

		String cfilesize = "filesize:" + String.valueOf(currfile.getFilesize());
		String cfiledescription = ",filedescription:" + currfile.getDescription();
		String cfilefigure = ",filefigure:" + currfile.getFigure();
		String cfilepanel = ",filepanel:" + currfile.getPanel();
		String cfilename = ",filename:" + currfile.getFilename();
		
		
		allstrings = cfilesize + cfiledescription + cfilefigure + cfilepanel + cfilename;
		
		filesanddata.add(allstrings);

	}

}
public void setSOLRVariablesCombined()
{

	
	 filesanddata = new ArrayList<String> ();
	String allstrings = "";
	for(FileStorer currfile: selectedpub.getPubfiles())
	{
	
		String cfilesize = "filesize:" + String.valueOf(currfile.getFilesize());
		String cfiledescription = ",filedescription:" + currfile.getDescription();
		String cfilefigure = ",filefigure:" + currfile.getFigure();
		String cfilepanel = ",filepanel:" + currfile.getPanel();
		String cfilename = ",filename:" + currfile.getFilename();
		
		
		allstrings = cfilesize + cfiledescription + cfilefigure + cfilepanel + cfilename;
		
		filesanddata.add(allstrings);
	
	}
}




//update SOLR with the user's draftpoint save point 1
public void draftPointSave1()
{
	
	setSOLRVariablesCombined();

	try
	{

	setSOLRMetadata();
	metadoc.addField("completion", "false");
	metadoc.addField("draftpoint", "1" );

    	
	server.add(metadoc);
	server.commit();
	}
	catch(Exception ex)
	{
		logger.info(ex);
	}
	
	
}


//update SOLR with the user's draftpoint save point 2
public void draftPointSave2()
{
	setSOLRVariablesCombined();

	try
	{

	 setSOLRMetadata();

	 metadoc.addField("completion", "false");
	 metadoc.addField("draftpoint", "2" );
	
	 server.add(metadoc);
	 server.commit();
	}
	catch(Exception ex)
	{
		logger.info(ex);
	}
}
public void draftPointSave3()
{
	setSOLRVariablesCombined();

	try
	{
		setSOLRMetadata();
	   	metadoc.addField("completion", "true");
		metadoc.addField("draftpoint", "2" );

	    server.add(metadoc);
	    server.commit();
	}
	catch(Exception ex)
	{
		logger.info(ex);
	}
}
@SuppressWarnings("unchecked")
public void getUserEntries()
{

		  //Get this user's ID

			User currentUser = LiferayFacesContext.getInstance().getUser();
			String userId = Long.toString(currentUser.getUserId());
		  
		  //search solr with for all results with this user's ID

				 try
				 {
					
					 CoreAdminRequest adminRequest = new CoreAdminRequest();
					 adminRequest.setAction(CoreAdminAction.RELOAD);

					 SolrServer solr = new HttpSolrServer ("http://localhost:8983/solr");

					 String query;

					 query = "lruid:" + userId;
					 
					
					 SolrQuery theq = new SolrQuery();
					 theq.setQuery(query);
					 theq.setStart(0);
					 theq.setRows(10000);
					 
					 QueryResponse response = new QueryResponse();

					 response = solr.query(theq);

					 SolrDocumentList list = response.getResults();
				

					 int docnum = 1;
					 for(SolrDocument doc: list)
					 {
						Publication currlist = new Publication();
						
						 List<String> fullnames =  new ArrayList<String> ();
						 String currepubsum1 = "", currepubsum2 = "";
						
						currlist.setTitle(doc.getFieldValue("ptitle").toString());
						currlist.setAbstract(doc.getFieldValue("abstract").toString());
					    currlist.setPmid(Integer.valueOf(doc.getFieldValue("pmid").toString()));
						
					    pmid = String.valueOf(currlist.getPmid());
						
						if(doc.getFieldValue("journalname")!=null)
						{
						currlist.setJournalname(doc.getFieldValue("journalname").toString());
						}
						
						if(doc.getFieldValue("journalyear")!=null)
						{
						currlist.setJournalyear(doc.getFieldValue("journalyear").toString());
						}
						if(doc.getFieldValue("journalday")!=null)
						{
						currlist.setJournalday(doc.getFieldValue("journalday").toString());
						}
						if(doc.getFieldValue("journalmonth")!=null)
						{
						currlist.setJournalmonth(doc.getFieldValue("journalmonth").toString());
						}
						if(doc.getFieldValue("journalpage")!=null)
						{
						currlist.setJournalstartpg(doc.getFieldValue("journalpage").toString());
						}
						if(doc.getFieldValue("journalissue")!=null)
						{
						currlist.setJournalissue(doc.getFieldValue("journalissue").toString());
						}
						if(doc.getFieldValue("journalvolume")!=null)
						{
						currlist.setJournalvolume(doc.getFieldValue("journalvolume").toString());
						}
						if(doc.getFieldValue("publicationdate_year")!=null)
						{
						currlist.setYear(doc.getFieldValue("publicationdate_year").toString());
						}
						if(doc.getFieldValue("doi") != null)
						{
						currlist.setDoi(doc.getFieldValue("doi").toString());
						}
						
						if(doc.getFieldValues("pfileinfo") != null)
						{
						
							Collection<Object> currcoll = doc.getFieldValues("pfileinfo");
							
							for(Object currobj: currcoll)
							{
								convertStore(String.valueOf(currobj), currlist);
							}
							
						}
						if(doc.getFieldValue("author_firstname") != null)
						{
						currlist.setFauthors((List<String>) doc.getFieldValue("author_firstname"));
						}
						if(doc.getFieldValue("author_lastname") != null)
						{
						currlist.setLauthors((List<String>) doc.getFieldValue("author_lastname"));
						}
						
						if(doc.getFieldValue("epubmonth") != null)
						{
						currlist.setEpubmonth(doc.getFieldValue("epubmonth").toString());
						}
						
						if(doc.getFieldValue("epubyear") != null)
						{
						currlist.setEpubyear(doc.getFieldValue("epubyear").toString());
						}
						if(doc.getFieldValue("epubday") !=null)
						{
						currlist.setEpubday(doc.getFieldValue("epubday").toString());
						}
						if(doc.getFieldValue("author_fullname_list") !=null)
						{
							
							currlist.setAuthorfull(doc.getFieldValue("author_fullname_list").toString());
						}
					
						int counter = 0;
						
						for(String currstring: currlist.getFauthors())
						{
						    currstring += " " + currlist.getLauthors().get(counter); 
						    fullnames.add(currstring);
							counter++;
						}
						
						currlist.setFullnames(fullnames);
						
						if(currlist.getJournalvolume().length()>0)
		  	        	{
		  	        		currepubsum2 +=  currlist.getJournalvolume();
		  	        	}
		  	        	
		  	        	if(currlist.getJournalissue().length()>0)
		  	        	{
		  	        		currepubsum2 += "("+ currlist.getJournalissue() + ")"+ ":";
		  	        	}
		  	        	
		  	        	if(currlist.getJournalstartpg().length()>0)
		  	        	{
		  	        		currepubsum2 += currlist.getJournalstartpg() + ".";
		  	        	}

			              
		  	        	if( currlist.getEpubday().length()<1 && currlist.getEpubmonth().length()<1  && currlist.getEpubyear().length()<1)
		  	        	{
		  	        		currepubsum1 = "[Epub ahead of print]"; 
	  	            	 }
		  	        	else if(currlist.getEpubyear().length()>0)
		  	        	{
		  	        		currepubsum1= "Epub "  + currlist.getEpubyear() + " " + currlist.getEpubmonth() + " " + currlist.getEpubday();
		  	        	}
		  	        	else
		  	        	{
		  	        		currepubsum1 = "";
		  	        	}
						
			              currlist.setEpubsum(currepubsum1);
			              currlist.setEpubsum2(currepubsum2);
			              currlist.setIndex(docnum);

			              currlist.setTitle(doc.getFieldValue("ptitle").toString());
			              currlist.setFirst5authors(doc.getFieldValue("author_fullname_list").toString());
			              currlist.setPmid(Integer.valueOf(doc.getFieldValue("pmid").toString()));
			              currlist.setCompletion(Boolean.valueOf(doc.getFieldValue("completion").toString()));
						
						if(currlist.getCompletion() == false)
						{
							currlist.setComp("No");
						}
						else
						{
							currlist.setComp("Yes");
						}

						publications.add(currlist);
						docnum++;
					 }

				 }
				 catch (Exception ex)
				 {
					logger.info(ex);
					logger.error("Failed!", ex);

				 }
		  

	  }

public void setSOLRMetadata()
{
	 CoreAdminRequest adminRequest = new CoreAdminRequest();
	 adminRequest.setAction(CoreAdminAction.RELOAD);

	 server = new HttpSolrServer ("http://localhost:8983/solr");
	 
	metadoc = new SolrInputDocument();
	 
	 metadoc.addField("pmid", selectedpub.getPmid());
	 //metadoc.addField("pid", UUID.randomUUID());
	 metadoc.addField("abstract", selectedpub.getAbstract());
	 metadoc.addField("publicationdate_year", selectedpub.getYear());
	 metadoc.addField("doi", selectedpub.getDoi());
	 metadoc.addField("journalvolume", selectedpub.getJournalvolume());
  	 metadoc.addField("journalissue", selectedpub.getJournalissue());
  	 metadoc.addField("journalmonth", selectedpub.getJournalmonth());
  	 metadoc.addField("journalyear", selectedpub.getJournalyear());
  	 metadoc.addField("journalday", selectedpub.getJournalday());
  	 metadoc.addField("journalname", selectedpub.getJournalname());
  	 metadoc.addField("journalpage", selectedpub.getJournalstartpg());
  	 metadoc.addField("epubday", selectedpub.getEpubday());
  	 metadoc.addField("epubmonth", selectedpub.getEpubmonth());
  	 metadoc.addField("epubyear", selectedpub.getEpubyear());
  	 metadoc.addField("author_fullname_list", selectedpub.getAuthorfull());
     metadoc.addField("ptitle", selectedpub.getTitle() );  
	   
 	  for(int i=0; i<selectedpub.getFauthors().size(); i++) 
 	  {
 	     metadoc.addField("author_firstname",selectedpub.getFauthors().get(i));
 	     metadoc.addField("author_lastname",selectedpub.getLauthors().get(i)); 
 	  }

	  
		 for(String currstring: filesanddata)
		 {
		  metadoc.addField("pfileinfo",currstring);
		 }
		 metadoc.addField("lruid", userId);
 	  
}


	  
	private void RetrieveFiles() {
			 
			int currpmid =  selectedpub.getPmid();
			pmid = String.valueOf(currpmid);
	    	String currlocation = PropsUtil.get("data_store2") + this.selectedpub.getPmid() + "/";
	    	File folder = new File(currlocation);

	    	for(File currfile: folder.listFiles())
	    	{
	    		
	           String absolutePath = currfile.getAbsolutePath();
	    		FileStorer currfilestore = new FileStorer();
	    		currfilestore.setFilename(currfile.getName());
	    		currfilestore.setFilelocation(currlocation);
	    		currfilestore.setFiletype(FilenameUtils.getExtension(currfile.getName()));
	    		currfilestore.setLocalfilestore( absolutePath.substring(0,absolutePath.lastIndexOf(File.separator)));
	    		allfiles.add(currfilestore);
	    		files.add(currfile);
	    		filenames.add(currfile.getName());
	    	}
	    	
	    	selectedpub.setFiles(files);
	    	selectedpub.setFilenames(filenames);

		}
	 
	  public void combineFiles()
	  {
		  for(FileStorer currfile: fchooser.getNewrefinedfiles())
		  {
			  selectedpub.getPubfiles().add(currfile);
		  }

	  }
	  
	  public void convertStore(String fileinfo, Publication currlist)
		{
			int fname, fsize, ffigure, fpanel, fdescription = -1;
			String sname , ssize, sfigure, spanel, sdescription;
			sname = ssize= sfigure= spanel= sdescription = "";
			
			
			fsize = fileinfo.indexOf("filesize:");
			fdescription = fileinfo.indexOf(",filedescription:");
			ffigure = fileinfo.indexOf(",filefigure:");
			fpanel = fileinfo.indexOf(",filepanel:");
			fname = fileinfo.indexOf(",filename:");
			
			if(fsize != -1 && fdescription != -1)
			{
				ssize = (String) fileinfo.subSequence(fsize, fdescription);
			}
			if(ffigure!= -1 && fdescription != -1)
			{
				sdescription = (String) fileinfo.subSequence(fdescription, ffigure);
			}
			if(ffigure!= -1 && fpanel != -1)
			{
				sfigure = (String) fileinfo.subSequence(ffigure, fpanel);
			}
			if(fname != -1 && fpanel != -1)
			{
				spanel = (String) fileinfo.subSequence(fpanel, fname);
			}
			if(fname != -1)
			{
				sname = (String) fileinfo.subSequence(fname,fileinfo.length());
			}
			
			ssize = ssize.replace("filesize:","" );
			sname = sname.replace(",filename:", "");
			sdescription = sdescription.replace(",filedescription:", "");
			sfigure = sfigure.replace(",filefigure:", "");
			spanel = spanel.replace(",filepanel:", "");
			
			
			
			String fileloc = PropsUtil.get("data_store2") + pmid + "/";
			FileStorer currfile = new FileStorer();
			currfile.setDescription(sdescription);
			currfile.setFigure(sfigure);
			currfile.setFilesize(Long.valueOf(ssize));
			currfile.setFilename(sname);
			currfile.setPanel(spanel);
			currfile.setIndex(solrindex);
			currfile.setFilelocation(fileloc);
			currfile.setLocalfilestore(fileloc);
			
			
			if(!currfile.getFilename().equals(pmid + ".zip"))
			{
			currlist.getPubfiles().add(currfile);
			}
			
			
			solrindex++;
			
		}
		
	  
	  
	  
	  
}
