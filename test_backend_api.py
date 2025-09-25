#!/usr/bin/env python3

import requests
import json

def test_truth_table_api():
    """Test the truth table API to examine the LaTeX output"""

    # Test with a complex formula that might have duplicate subformulas
    test_cases = [
        {
            'name': 'Simple formula - DETAILED',
            'formulas': ['p \\wedge q'],
            'detailed': True
        },
        {
            'name': 'Simple formula - SIMPLE',
            'formulas': ['p \\wedge q'],
            'detailed': False
        },
        {
            'name': 'Complex formula - DETAILED',
            'formulas': ['(p \\wedge q) \\vee \\neg r'],
            'detailed': True
        },
        {
            'name': 'Complex formula - SIMPLE',
            'formulas': ['(p \\wedge q) \\vee \\neg r'],
            'detailed': False
        }
    ]

    for test_case in test_cases:
        print(f"\n=== Testing: {test_case['name']} ===")
        print(f"Formula: {test_case['formulas'][0]}")
        print(f"Detailed: {test_case['detailed']}")

        try:
            response = requests.post(
                'http://localhost:8080/api/truth-table/generate',
                headers={'Content-Type': 'application/json'},
                json={
                    'formulas': test_case['formulas'],
                    'detailed': test_case['detailed'],
                    'checkType': True
                }
            )

            if response.status_code == 200:
                data = response.json()
                if 'latexTable' in data:
                    print("\nLaTeX Table Output:")
                    print(data['latexTable'])

                    # Analyze the table structure
                    latex = data['latexTable']
                    if '\\begin{array}' in latex:
                        # Extract the content between \begin{array} and \end{array}
                        import re
                        array_match = re.search(r'\\begin\{array\}\{([^}]*)\}([\\s\\S]*?)\\end\{array\}', latex)
                        if array_match:
                            columns_spec = array_match.group(1)
                            content = array_match.group(2)

                            print(f"\nColumn specification: {columns_spec}")
                            print(f"Number of columns: {len(columns_spec)}")

                            # Split into rows
                            rows = content.split('\\\\')
                            rows = [row.strip() for row in rows if row.strip()]

                            print(f"Number of rows: {len(rows)}")

                            # Analyze header row (first row)
                            if rows:
                                header_cells = [cell.strip() for cell in rows[0].split('&')]
                                print(f"Header cells: {header_cells}")

                                # Check for duplicates in header
                                seen = set()
                                duplicates = set()
                                for i, cell in enumerate(header_cells):
                                    if cell in seen:
                                        duplicates.add(cell)
                                        print(f"DUPLICATE FOUND: '{cell}' at position {i}")
                                    seen.add(cell)

                                if duplicates:
                                    print(f"Duplicate subformulas found: {duplicates}")
                                else:
                                    print("No duplicate subformulas found in header")

                    print(f"Formula type: {data.get('formulaType', 'N/A')}")
                else:
                    print("Error in response:", data.get('errorMessage', 'Unknown error'))
            else:
                print(f"HTTP Error: {response.status_code}")
                print(response.text)

        except Exception as e:
            print(f"Exception: {e}")

if __name__ == "__main__":
    test_truth_table_api()