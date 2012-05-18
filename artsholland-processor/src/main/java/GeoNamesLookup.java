import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;


public class GeoNamesLookup {

	public static void main(String[] args) throws Exception {
		WebService.setUserName("artsholland"); // add your username here

		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		searchCriteria.setQ("Parkstraat Arnhem");
		searchCriteria.setCountryBias("NL");
		ToponymSearchResult searchResult = WebService.search(searchCriteria);
		for (Toponym toponym : searchResult.getToponyms()) {
			System.out.println(toponym.getName() + " "
					+ toponym.getCountryName());
		}
	}
}
