package example.nosql;

import java.io.File;
import java.text.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Part;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

@Path("/favorites")
/**
 * CRUD service of todo list table. It uses REST style.
 */
public class ResourceServlet {

	public ResourceServlet() {
	}

	@POST
	public Response create(@FormParam("id") Long id, @FormParam("name") String name, @FormParam("value") String value, @FormParam("app_cyc_date") String app_cyc_date, @FormParam("app_cyc_compl_date") String app_cyc_compl_date, @FormParam("app_rf") String app_rf, @FormParam("app_sob") String app_sob, @FormParam("app_problem") String app_problem)
			throws Exception {

		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		String idString = id == null ? null : id.toString();
		JsonObject resultObject = create(db, idString, name, value, null, null, app_cyc_date, app_cyc_compl_date, app_rf, app_sob, app_problem);

		System.out.println("Create Successful.");

		return Response.ok(resultObject.toString()).build();
	}

	protected JsonObject create(Database db, String id, String name, String value, String fileName, Part part, String app_cyc_date, String app_cyc_compl_date, String app_rf, String app_sob, String app_problem)
			throws IOException {

		// check if document exist
		//HashMap<String, Object> obj = (id == null) ? null : db.find(HashMap.class, id);
		 
		HashMap<String, Object> obj = new HashMap<String, Object>();

		//if (obj == null) {
			// if new document

			id = String.valueOf(System.currentTimeMillis());

			// create a new document
		  	System.out.println("Creating new document with id : " + id);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", name);
			data.put("_id", id);
			data.put("value", value);
			data.put("creation_date", new Date().toString());
			db.save(data);

			// attach the attachment object
		//	obj = db.find(HashMap.class, id);
		//	saveAttachment(db, id, part, fileName, obj);
	//	} else {
			// if existing document
			// attach the attachment object
		//	saveAttachment(db, id, part, fileName, obj);

			// update other fields in the document
		//	obj = db.find(HashMap.class, id);
		//	obj.put("name", name);
		//	obj.put("value", value);
		//	obj.put("app_cyc_date",app_cyc_date);
		//	obj.put("app_cyc_compl_date",app_cyc_compl_date);
		//	obj.put("app_rf",app_rf);
		//	obj.put("app_sob",app_sob);
		//	obj.put("app_problem",app_problem);
		//	db.update(obj);
	//	}

		obj = db.find(HashMap.class, id);

		JsonObject resultObject = toJsonObject(obj);

		return resultObject;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@QueryParam("id") Long id, @QueryParam("cmd") String cmd) throws Exception {

		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		JsonObject resultObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		if (id == null) {
			try {
				// get all the document present in database
				List<HashMap> allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
						.getDocsAs(HashMap.class);

				if (allDocs.size() == 0) {
					allDocs = initializeSampleData(db);
				}

				for (HashMap doc : allDocs) {
					HashMap<String, Object> obj = db.find(HashMap.class, doc.get("_id") + "");
					JsonObject jsonObject = new JsonObject();
					LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");

					if (attachments != null && attachments.size() > 0) {
						JsonArray attachmentList = getAttachmentList(attachments, obj.get("_id") + "");
						jsonObject.addProperty("id", obj.get("_id") + "");
						jsonObject.addProperty("name", obj.get("name") + "");
						jsonObject.addProperty("value", obj.get("value") + "");
						jsonObject.addProperty("app_cyc_date", obj.get("app_cyc_date") + "");
						jsonObject.addProperty("app_cyc_compl_date", obj.get("app_cyc_compl_date") + "");
						jsonObject.addProperty("app_rf", obj.get("app_rf") + "");
						jsonObject.addProperty("app_sob", obj.get("app_sob") + "");
						jsonObject.addProperty("app_problem", obj.get("app_problem") + "");
						jsonObject.add("attachements", attachmentList);

					} else {
						jsonObject.addProperty("id", obj.get("_id") + "");
						jsonObject.addProperty("name", obj.get("name") + "");
						jsonObject.addProperty("value", obj.get("value") + "");
						jsonObject.addProperty("app_cyc_date", obj.get("app_cyc_date") + "");
						jsonObject.addProperty("app_cyc_compl_date", obj.get("app_cyc_compl_date") + "");
						jsonObject.addProperty("app_rf", obj.get("app_rf") + "");
						jsonObject.addProperty("app_sob", obj.get("app_sob") + "");
						jsonObject.addProperty("app_problem", obj.get("app_problem") + "");
					}

					jsonArray.add(jsonObject);
				}
			} catch (Exception dnfe) {
				System.out.println("Exception thrown : " + dnfe.getMessage());
			}

			resultObject.addProperty("id", "all");
			resultObject.add("body", jsonArray);

			return Response.ok(resultObject.toString()).build();
		}

		// check if document exists
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");
		if (obj != null) {
			JsonObject jsonObject = toJsonObject(obj);
			return Response.ok(jsonObject.toString()).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	public Response delete(@QueryParam("id") long id) {

		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		// check if document exist
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");

		if (obj == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			db.remove(obj);

			System.out.println("Delete Successful.");

			return Response.ok("OK").build();
		}
	}

	@PUT
	public Response update(@QueryParam("id") long id, @QueryParam("name") String name,
			@QueryParam("value") String value, @QueryParam("app_cyc_date") String app_cyc_date, @QueryParam("app_cyc_compl_date") String app_cyc_compl_date, @QueryParam("app_rf") String app_rf, @QueryParam("app_sob") String app_sob, @QueryParam("app_problem") String app_problem) {

		Database db = null;
		try {
			db = getDB();
		} catch (Exception re) {
			re.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		// check if document exist
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");

		if (obj == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else {
			obj.put("name", name);
			obj.put("value", value);
			obj.put("app_cyc_date", app_cyc_date);
			obj.put("app_cyc_compl_date", app_cyc_compl_date);
			obj.put("app_rf", app_rf );
			obj.put("app_sob", app_sob);
			obj.put("app_problem", app_problem);

			db.update(obj);

			System.out.println("Update Successful.");

			return Response.ok("OK").build();
		}
	}

	private JsonArray getAttachmentList(LinkedTreeMap<String, Object> attachmentList, String docID) {
		JsonArray attachmentArray = new JsonArray();

		for (Object key : attachmentList.keySet()) {
			LinkedTreeMap<String, Object> attach = (LinkedTreeMap<String, Object>) attachmentList.get(key);

			JsonObject attachedObject = new JsonObject();
			// set the content type of the attachment
			attachedObject.addProperty("content_type", attach.get("content_type").toString());
			// append the document id and attachment key to the URL
			attachedObject.addProperty("url", "attach?id=" + docID + "&key=" + key);
			// set the key of the attachment
			attachedObject.addProperty("key", key + "");

			// add the attachment object to the array
			attachmentArray.add(attachedObject);
		}

		return attachmentArray;
	}

	private JsonObject toJsonObject(HashMap<String, Object> obj) {
		JsonObject jsonObject = new JsonObject();
		LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");
		if (attachments != null && attachments.size() > 0) {
			JsonArray attachmentList = getAttachmentList(attachments, obj.get("_id") + "");
			jsonObject.add("attachements", attachmentList);
		}
		jsonObject.addProperty("id", obj.get("_id") + "");
		jsonObject.addProperty("name", obj.get("name") + "");
		jsonObject.addProperty("value", obj.get("value") + "");
		jsonObject.addProperty("app_cyc_date", obj.get("app_cyc_date") + "");
		jsonObject.addProperty("app_cyc_compl_date", obj.get("app_cyc_compl_date") + "");
		jsonObject.addProperty("app_rf", obj.get("app_rf") + "");
		jsonObject.addProperty("app_sob", obj.get("app_sob") + "");
		jsonObject.addProperty("app_problem", obj.get("app_problem") + "");
		return jsonObject;
	}

	private void saveAttachment(Database db, String id, Part part, String fileName, HashMap<String, Object> obj)
			throws IOException {
		if (part != null) {
			InputStream inputStream = part.getInputStream();
			try {
				db.saveAttachment(inputStream, URLEncoder.encode(fileName,"UTF-8"), part.getContentType(), id, (String) obj.get("_rev"));
			} finally {
				inputStream.close();
			}
		}
	}

	/*
	 * Create a document and Initialize with sample data/attachments
	 */
	private List<HashMap> initializeSampleData(Database db) throws Exception {

		long id = System.currentTimeMillis();
		String name = "PW";
		String value = "Sample application";
		Date date = new Date();
		String app_cyc_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
		String app_cyc_compl_date = app_cyc_date + 1;
	    String app_rf = "Aishwarya";
	    String app_sob = "Completed";
	    String app_problem = "None";
	    
		// create a new document
		System.out.println("Creating new document with id : " + id);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("_id", id + "");
		data.put("value", value);
		data.put("app_cyc_date",app_cyc_date);
		data.put("app_cyc_compl_date",app_cyc_compl_date);
		data.put("app_rf",app_rf);
		data.put("app_sob",app_sob);
		data.put("app_problem",app_problem);
		data.put("creation_date", new Date().toString());
		db.save(data);

		// attach the object
		HashMap<String, Object> obj = db.find(HashMap.class, id + "");

		// attachment#1
		File file = new File("Sample.txt");
		file.createNewFile();
		PrintWriter writer = new PrintWriter(file);
		writer.write("This is a sample file...");
		writer.flush();
		writer.close();
		FileInputStream fileInputStream = new FileInputStream(file);
		db.saveAttachment(fileInputStream, file.getName(), "text/plain", id + "", (String) obj.get("_rev"));
		fileInputStream.close();

		return db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(HashMap.class);

	}

	private Database getDB() {
		return CloudantClientMgr.getDB();
	}

}
