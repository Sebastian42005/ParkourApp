import Foundation

struct SpotRequest: Codable, Hashable {
    let title: String
    let description: String
    let latitude: Double
    let longitude: Double
    let city: String
    let attributes: [String]
    let sport: String
    
    init(title: String, description: String, latitude: Double, longitude: Double, city: String, attributes: [String], sport: String) {
        self.title = title
        self.description = description
        self.latitude = latitude
        self.longitude = longitude
        self.city = city
        self.attributes = attributes
        self.sport = sport
    }
}
