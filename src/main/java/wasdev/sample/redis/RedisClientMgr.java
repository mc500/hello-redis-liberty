package wasdev.sample.redis;

import java.util.Map.Entry;
import java.util.Set;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RedisClientMgr {
	
	private static RedisClient redisClient = null;
	private static StatefulRedisConnection<String, String> redisConnection = null;

	private static String hostname = "REPLACE_WITH_REDIS_HOSTNAME";
	private static String port = "REPLACE_WITH_REDIS_PORT";
	private static String password = "REPLACE_WITH_REDIS_PASSWORD";
	
	private static void initClient() {
		if (redisClient == null) {
			synchronized (RedisClientMgr.class) {
				if (redisClient != null) {
					return;
				}
				redisClient = createClient();				
			}
		}
	}
	
	private static RedisClient createClient() {
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		String serviceName = null;

		if (VCAP_SERVICES != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.
			JsonObject obj = (JsonObject) new JsonParser().parse(VCAP_SERVICES);
			Entry<String, JsonElement> dbEntry = null;
			Set<Entry<String, JsonElement>> entries = obj.entrySet();
			// Look for the VCAP key that holds the Redis Cloud information
			for (Entry<String, JsonElement> eachEntry : entries) {
				if (eachEntry.getKey().toLowerCase().contains("rediscloud")) {
					dbEntry = eachEntry;
					break;
				}
			}
			if (dbEntry == null) {
				throw new RuntimeException("Could not find rediscloud key in VCAP_SERVICES env variable");
			}

			obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
			serviceName = (String) dbEntry.getKey();
			System.out.println("Service Name - " + serviceName);

			obj = (JsonObject) obj.get("credentials");

			hostname = obj.get("hostname").getAsString();
			port = obj.get("port").getAsString();
			password = obj.get("password").getAsString();

		} else {
			System.out.println("VCAP_SERVICES env var doesn't exist: running locally.");
		}

		try {
			System.out.println("Connecting to Redis cloud : "+hostname);
			RedisURI redisURI = RedisURI.Builder.redis(hostname)
                    .withPassword(password)
                    .withPort(Integer.valueOf(port))
                    .build();
			RedisClient client = RedisClient.create(redisURI);
			return client;
		} catch (Exception e) {
			throw new RuntimeException("Unable to connect to Redis cloud", e);
		}
	}
	
	public static StatefulRedisConnection<String, String> getConnection() {
		if (redisClient == null) {
			initClient();
		}

		if (redisConnection == null) {
			try {
				redisConnection = redisClient.connect();
			} catch (Exception e) {
				throw new RuntimeException("Connection failed", e);
			}
		}
		return redisConnection;
	}

	private RedisClientMgr() {
		
	}
}
