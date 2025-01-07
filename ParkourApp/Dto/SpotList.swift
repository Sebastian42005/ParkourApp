import Foundation

struct SpotList: Codable, Hashable {
    let spots: [Spot]
    let hasMore: Bool
}
