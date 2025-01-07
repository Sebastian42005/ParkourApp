import Foundation
import SwiftUI

struct FancyToastView: View {
    var type: FancyToastStyle
    var title: String
    var message: String
    var onCancelTapped: (() -> Void)
    var body: some View {
        VStack(alignment: .leading) {
            HStack(alignment: .top) {
                Image(systemName: type.iconFileName)
                    .resizable()
                    .frame(width: 28, height: 28)
                    .foregroundColor(type.themeColor)
                    .padding(.top, 5)
                
                VStack(alignment: .leading) {
                    Text(title)
                        .font(.system(size: 17, weight: .semibold))
                    
                    Text(message)
                        .font(.system(size: 15))
                        .foregroundColor(Color.black.opacity(0.6))
                }
                
                Spacer(minLength: 10)
                
                Button {
                    onCancelTapped()
                } label: {
                    Image(systemName: "xmark")
                        .foregroundColor(Color.black)
                }
            }
            .padding()
        }
        .background(Color.white)
        .overlay(
            Rectangle()
                .fill(type.themeColor)
                .frame(width: 6)
                .clipped()
            , alignment: .leading
        )
        .frame(minWidth: 0, maxWidth: .infinity)
        .cornerRadius(8)
        .shadow(color: Color.black.opacity(0.25), radius: 4, x: 0, y: 1)
        .padding(.horizontal, 16)
    }
}

struct FancyToast: Equatable {
    var type: FancyToastStyle
    var title: String
    var message: String
    var duration: Double = 3
}
