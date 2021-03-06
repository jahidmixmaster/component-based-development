package de.htwBerlin.ai.kbe.services;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.htwBerlin.ai.kbe.bean.Contact;
import de.htwBerlin.ai.kbe.storage.AddressBook;

// URL fuer diesen Service ist: http://localhost:8080/helloJAXRS/rest/contacts 
@Path("/contacts")
public class ContactsWebService {

	//GET http://localhost:8080/helloJAXRS/rest/contacts
	//Returns all contacts
	@GET 
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Collection<Contact> getAllContacts() {
		System.out.println("getAllContacts: Returning all contacts!");
		return AddressBook.getInstance().getAllContacts();
	}

	//GET http://localhost:8080/helloJAXRS/rest/contacts/1
	//Returns: 200 and contact with id 1
	//Returns: 404 on provided id not found
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getContact(@PathParam("id") Integer id) {
		Contact contact = AddressBook.getInstance().getContact(id);
		if (contact != null) {
			System.out.println("getContact: Returning contact for id " + id);
			return Response.ok(contact).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).entity("No contact found with id " + id).build();
		}
	}

	// POST http://localhost:8080/helloJAXRS/rest/contacts with contact in payload
	// Returns: Status Code 201 and the new id of the contact in the payload 
	// (temp. solution)
	//
	// Besser: Status Code 201 und URI fuer den neuen Eintrag im http-header 'Location' zurueckschicken, also:
	// Location: /helloJAXRS/rest/contacts/neueID
	// Aber dazu brauchen wir "dependency injection", also spaeter
	// @Context UriInfo uriInfo
	// return Response.created(uriInfo.getAbsolutePath()+<newId>).build(); 
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces(MediaType.TEXT_PLAIN)
	public Response createContact(Contact contact) {
		System.out.println("createContact: Received contact: " + contact.toString());
		return Response.status(Response.Status.CREATED).entity(AddressBook.getInstance().addContact(contact)).build();
	}
	
	//PUT http://localhost:8080/helloJAXRS/rest/contacts/1 with updated contact in payload
    //Returns 204 on successful update
	//Returns 404 on contact with provided id not found
	//Returns 400 on id in URL does not match id in contact
	@PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/{id}")
    public Response updateSong(@PathParam("id") Integer id, Contact contact) {
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity("PUT not implemented").build();
    }
	
	//DELETE http://localhost:8080/helloJAXRS/rest/contacts/1
    //Returns 204 on successful delete
	//Returns 404 on provided id not found
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Integer id) {
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity("DELETE not implemented").build();
	}
}