import SwiftUI

struct RatingList: View {
    var ratings: [RatingDto]?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            if let ratings = ratings {
                if !ratings.isEmpty {
                    ForEach(ratings, id: \.id) { rating in
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(rating.username)
                                    .font(.headline)
                                    .foregroundColor(.textColor)
                                Rating(stars: rating.stars, starSize: 20)
                            }
                            if !rating.message.isEmpty {
                                Text(rating.message)
                                    .font(.body)
                                    .foregroundColor(.textColor)
                            }
                            Divider()
                        }
                    }
                }
            } else {
                ForEach(0..<3, id: \.self) { _ in
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            SkeletonLoader()
                                .frame(width: 120, height: 16)
                            Rating(starSize: 20)
                        }
                        
                        SkeletonLoader()
                            .frame(width: .infinity, height: 14)
                            .padding(.trailing, 50)
                        Divider()
                    }
                }
            }
        }
        .padding(.vertical)
    }
}

#Preview {
    RatingList()
    .padding()
}
