package de.htwBerlin.ai.kbe.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import de.htwBerlin.ai.kbe.bean.SongLists;
import de.htwBerlin.ai.kbe.security.IAuthContainer;
import de.htwBerlin.ai.kbe.storage.ISongListsDAO;

//URL fuer diesen Service ist: http://localhost:8080/songsRX/rest/userId 
@Path("/userId")
public class SongListsWebService {

	private ISongListsDAO SongListsDao;
	@Inject
	private IAuthContainer authContainer;

	@Inject
	public SongListsWebService(ISongListsDAO dao) {
		this.SongListsDao = dao;

	}

	@GET
	@Path("/{id}/songLists")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Collection<SongLists> getAllSongLists(@HeaderParam("Authorization") String token,
			@PathParam("id") String id) {

		System.out.println("getAllSongLists: Returning all SongLists!");

		// return all if same user
		System.out.println(SongListsDao.findAllSongLists(id, false));
		if (authContainer.getUserIdByToken(token).equals(id)) {

			return SongListsDao.findAllSongLists(id, false);

		}
		return SongListsDao.findAllSongLists(id, true);
	}

	@GET
	@Path("/{id}/songLists/{songList_id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML , MediaType.TEXT_PLAIN})
	public Response getSongListById(@HeaderParam("Authorization") String token, @PathParam("id") String id,
			@PathParam("songList_id") Integer songListId) {

		System.out.println("getAllSongLists: Returning SongLists by given id !");

		SongLists s;
		if (authContainer.getUserIdByToken(token).equals(id)) {

			s = SongListsDao.findSongListById(id, songListId, false);

		} else {
			s = SongListsDao.findSongListById(id, songListId, true);
		}
		if (s == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("No SongList found with id " + songListId).build();
		} else {
			return Response.ok(s).build();
		}
	}

	@POST
	@Path("/{id}/songLists/")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces(MediaType.TEXT_PLAIN)
	public Response createSongLists(@HeaderParam("Authorization") String token, @PathParam("id") String id,
			SongLists SongLists) throws URISyntaxException {
		 
		System.out.println(SongLists);

		if (authContainer.getUserIdByToken(token).equals(id) && SongLists.getUser().getUserId().equals(authContainer.getUserIdByToken(token))) {
			if (SongLists != null && SongLists.getSongs() != null) {
				try {
				int res = SongListsDao.saveSongLists(SongLists);
				return Response.created(new URI("/songsRX/rest/userId/" + id + "/songLists/" + res)).build();
				}catch(Exception e){
					return Response.status(Response.Status.BAD_REQUEST).entity("Song doenstn't exists ").build();
				}
			}
		}
		return Response.status(Response.Status.UNAUTHORIZED).entity("Not authorized to save for other user ").build();

	}

	@DELETE
	@Path("/{id}/songLists/{list_id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response delete(@HeaderParam("Authorization") String token, @PathParam("id") String id,
			@PathParam("list_id") int list_id) {
		
		System.out.println(authContainer.getUserIdByToken(token));
		System.out.println(id);
		if (authContainer.getUserIdByToken(token).equals(id)) {
			
			if (SongListsDao.deleteSongLists(list_id)) {
				return Response.status(Response.Status.NO_CONTENT).entity("Sucessfully deleted SongLists").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Can't delete this SongLists. SongLists doesn't exists").build();
			}

		}
		return Response.status(Response.Status.UNAUTHORIZED).entity("Not authorized to delete  other users playlist ")
				.build();

	}

}
