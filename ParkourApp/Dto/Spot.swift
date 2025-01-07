import Foundation

struct Spot: Codable, Hashable {
    let id: Int
    let description: String
    let location: Location
    let spotTypes: [String]
    let user: String
    let createdAt: String
    let rating: Double
    let pictures: [Int8]
    
    init(id: Int, description: String, location: Location, spotTypes: [String], user: String, createdAt: String, rating: Double, pictures: [Int8]) {
        self.id = id
        self.description = description
        self.location = location
        self.spotTypes = spotTypes
        self.user = user
        self.createdAt = createdAt
        self.rating = rating
        self.pictures = pictures
    }
}
