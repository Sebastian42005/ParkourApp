import SwiftUI

struct ShimmerViewModifier: ViewModifier {
    @State private var isAnimating = false

        func body(content: Content) -> some View {
            content
                .opacity(isAnimating ? 0.5 : 1.0)
                .animation(Animation.easeInOut(duration: 0.8).repeatForever(autoreverses: true), value: isAnimating)
                .onAppear {
                    isAnimating = true
                }
        }
}

extension View {
    func shimmer() -> some View {
        self.modifier(ShimmerViewModifier())
    }
}
