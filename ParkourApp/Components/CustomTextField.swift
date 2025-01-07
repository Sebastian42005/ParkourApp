//
//  CustomTextField.swift
//  ParkourApp
//
//  Created by Sebastian Ederer on 28.10.24.
//

import SwiftUI

struct CustomTextField: View {
    @Binding var text: String
    var placeHolder: String
    var secure = false;
    var autocapitalization: UITextAutocapitalizationType = .none
    var body: some View {
        if secure {
            SecureField(placeHolder, text: $text)
                .padding(12)
                .background(Color.cardColor)
                .cornerRadius(10)
                .autocapitalization(autocapitalization)
                .accentColor(Color.primary)
        } else {
            TextField(placeHolder, text: $text)
                .padding(12)
                .background(Color.cardColor)
                .cornerRadius(10)
                .accentColor(Color.primary)
        }
    }
}
