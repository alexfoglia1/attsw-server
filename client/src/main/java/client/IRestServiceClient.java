package client;

import java.io.IOException;

public interface IRestServiceClient {

	public String doGet(int request, String args) throws IOException;
	public int getLastResponse();
	public String getHost();

}
