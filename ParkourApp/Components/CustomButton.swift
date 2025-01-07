//
//  CustomButton.swift
//  ParkourApp
//
//  Created by Sebastian Ederer on 28.10.24.
//

import SwiftUI

struct CustomButton: View {
    var iconName: String
    var buttonText: String
    var backgroundColor: Color = Color.primary
    var textColor: Color = Color.backgroundColor
    var action: () -> Void

    var body: some View {
            Button(action: {
                action()
            }) {
                HStack(spacing: 12) {
                    Text(buttonText)
                        .fontWeight(.bold)
                    Image(systemName: iconName)
                        .resizable()
                        .frame(width: 24, height: 24)
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
