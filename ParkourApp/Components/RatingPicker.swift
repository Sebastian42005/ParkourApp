import SwiftUI

struct RatingPicker: View {
    @Binding var stars: Int
    var starSize: CGFloat
    @State private var roundedStars: Double

    init(stars: Binding<Int>, starSize: CGFloat) {
        self._stars = stars
        self.starSize = starSize
        self._roundedStars = State(initialValue: RatingPicker.roundToHalf(stars.wrappedValue))
    }

    var body: some View {
        HStack(spacing: 5) {
            ForEach(0..<5, id: \.self) { i in
                let starFillType: String = {
                    if Double(i) + 0.5 < roundedStars {
                        return "star.fill"
                    } else if Double(i) < roundedStars {
                        return "star.leadinghalf.filled"
                    } else {
                        return "star"
                    }
                }()
                
                Image(systemName: starFillType)
                    .resizable()
                    .frame(width: starSize, height: starSize)
                    .foregroundColor(Double(i) < roundedStars ? .yellow : .gray)
                    .onTapGesture {
                        roundedStars = RatingPicker.roundToHalf(i + 1)
                        stars = Int(roundedStars)
                    }
            }
        }
    }

    static func roundToHalf(_ value: Int) -> Double {
        return (Double(value) * 2).rounded() / 2
    }
}

struct StatefulPreviewWrapper<Value>: View {
    @State private var value: Value
    private var content: (Binding<Value>) -> RatingPicker

    init(_ initialValue: Value, content: @escaping (Binding<Value>) -> RatingPicker) {
        self._value = State(initialValue: initialValue)
        self.content = content
    }

    var body: some View {
        content($value)
    }
}

#Preview {
    StatefulPreviewWrapper(3) { rating in
        RatingPicker(stars: rating, starSize: 40)
    }
}
