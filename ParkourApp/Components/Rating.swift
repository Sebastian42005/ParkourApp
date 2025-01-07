import SwiftUI

struct Rating: View {
    var stars: Double
    var starSize: CGFloat
    var body: some View {
        HStack(spacing: 5) {
            let roundedStars = Rating.roundToHalf(stars)
            ForEach(0..<5, id: \.self) { i in
                Group {
                    if Double(i) + 0.5 < roundedStars {
                        Image(systemName: "star.fill")
                            .resizable()
                    } else if Double(i) < roundedStars {
                        Image(systemName: "star.leadinghalf.filled")
                            .resizable()
                    } else {
                        Image(systemName: "star")
                            .resizable()
                    }
                }
                .frame(width: starSize, height: starSize)
                .foregroundColor(Double(i) < roundedStars ? .yellow : .gray)
            }
        }
    }

    static func roundToHalf(_ value: Double) -> Double {
        return (value * 2).rounded() / 2
    }
}

#Preview {
    Rating(stars: 3.6, starSize: 40)
}
