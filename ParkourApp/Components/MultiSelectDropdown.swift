import SwiftUI

struct MultiSelectDropdown<T: Hashable>: View {
    let options: [T]
    @Binding var selectedItems: Set<T>
    var displayText: (T) -> String
    var placeholder: String

    var body: some View {
        Menu {
            ForEach(options, id: \.self) { option in
                Button(action: {
                    toggleSelection(option)
                }) {
                    HStack {
                        Text(displayText(option))
                        Spacer()
                        if selectedItems.contains(option) {
                            Image(systemName: "checkmark")
                                .foregroundColor(.blue)
                        }
                    }
                }
            }
        } label: {
            HStack {
                Text(selectedItems.isEmpty ? placeholder : selectedItems.map(displayText).joined(separator: ", "))
                    .foregroundColor(selectedItems.isEmpty ? .gray : .primary)
                    .lineLimit(1)
                    .truncationMode(.tail)
                Image(systemName: "chevron.down")
                    .foregroundColor(.gray)
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(8)
        }
    }

    private func toggleSelection(_ option: T) {
        if selectedItems.contains(option) {
            selectedItems.remove(option)
        } else {
            selectedItems.insert(option)
        }
    }
}
