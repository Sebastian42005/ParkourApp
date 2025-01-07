import Foundation

struct Location: Codable, Hashable {
    let id: Int
    let latitude: Double
    let longitude: Double
    let city: String
}
