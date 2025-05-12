import SwiftUI

struct SkeletonLoader: View {
    var borderRadius: CGFloat = 10
    var body: some View {
        RoundedRectangle(cornerRadius: borderRadius)
            .fill(Color.gray.opacity(0.3))
            .shimmer()
    }
}

#Preview {
    SkeletonLoader()
}
