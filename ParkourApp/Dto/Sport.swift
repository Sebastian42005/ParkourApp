import Foundation
import SwiftUICore

struct Sport: Codable, Hashable {

    let name: String
    let symbol: String
    let color: String
    let attributes: [Attribute]
    
    func getColor() -> Color {
        return Color(hex: color)
    }
}
