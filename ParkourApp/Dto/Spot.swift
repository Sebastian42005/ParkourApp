import Foundation

struct Spot: Codable, Hashable {
    let id: Int
    let title: String
    let description: String
    let location: Location
    let sport: String
    let attributes: [Attribute]
    let user: String
    let createdAt: String
    let rating: Double
    let pictures: [Int8]
    
    init(id: Int, title: String, description: String, location: Location, sport: String, attributes: [Attribute], user: String, createdAt: String, rating: Double, pictures: [Int8]) {
        self.id = id
        self.title = title
        self.description = description
        self.location = location
        self.sport = sport
        self.attributes = attributes
        self.user = user
        self.createdAt = createdAt
        self.rating = rating
        self.pictures = pictures
    }
}
