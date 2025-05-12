import SwiftUI

struct CustomButton: View {
    var iconName: String
    var buttonText: String
    var fullWidth: Bool = false
    var backgroundColor: Color = Color.primary
    var textColor: Color = Color.textColor
    var action: () -> Void
    
    var body: some View {
        Button(action: {
            action()
        }) {
            HStack(spacing: 12) {
                if fullWidth {
                    Spacer()
                }
                Text(buttonText)
                    .fontWeight(.bold)
                if fullWidth {
                    Spacer()
                }
                Image(systemName: iconName)
                    .font(.system(size: 24))
            }
            .padding()
            .background(backgroundColor)
            .foregroundColor(textColor)
            .cornerRadius(10)
        }
        .padding()
    }
}


#Preview {
    CustomButton(iconName: "person.fill", buttonText: "Login") {
        
    }
}
