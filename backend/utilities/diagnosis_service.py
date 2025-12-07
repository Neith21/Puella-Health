import google.generativeai as genai
import os
from dotenv import load_dotenv
import json

def ia_agent(medical_data, pressions):
    genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))

    # Seleccionar el modelo
    model = genai.GenerativeModel('gemini-2.5-flash')

    # Tu prompt
    prompt = f""""
    
    Eres un asistente médico experto. Analiza los siguientes datos médicos del paciente y proporciona un diagnóstico preliminar y recomendaciones basadas en la información proporcionada.

    Datos Médicos del Paciente:
    {medical_data}

    Presiones:
    {pressions}

    Proporciona un diagnóstico preliminar y recomendaciones en un formato claro y conciso.
    
    """

    try:
        # Enviar el mensaje
        response = model.generate_content(prompt)
        
        # Mostrar la respuesta
        return response.text

    except Exception as e:
        raise f"Error al comunicarse con la API de Google Generative AI: {str(e)}"