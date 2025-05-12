
import SwiftUI

struct RatingPicker: View {
    @Binding var rating: Int?
    var starSize: CGFloat

    var body: some View {
        HStack(spacing: 5) {
            ForEach(0..<5, id: \.self) { i in
                let starFillType = i < rating ?? 0 ? "star.fill" : "star"
                Image(systemName: starFillType)
                    .resizable()
                    .frame(width: starSize, height: starSize)
                    .foregroundColor(i < rating ?? 0 ? .primary : .gray)
                    .onTapGesture {
                        rating = i + 1
                    }
            }
        }
    }
}
