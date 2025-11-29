from django.db import models
from patient.models import Patient

# Create your models here.
class PatientMedicalData(models.Model):
    BLOOD_TYPE_CHOICES = [
        ('A+', 'A+'),
        ('A-', 'A-'),
        ('B+', 'B+'),
        ('B-', 'B-'),
        ('AB+', 'AB+'),
        ('AB-', 'AB-'),
        ('O+', 'O+'),
        ('O-', 'O-'),
    ]
    
    patient_height_cm = models.DecimalField(max_digits=5, decimal_places=2, null=True, blank=True)
    patient_weight_kg = models.DecimalField(max_digits=5, decimal_places=2, null=True, blank=True)
    patient_blood_type = models.CharField(max_length=3, choices=BLOOD_TYPE_CHOICES, null=True, blank=True)
    patient_allergies = models.TextField(blank=True, null=True)
    patient_existing_conditions = models.TextField(blank=True, null=True)
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE)
    record_active = models.BooleanField(default=True)

    def __str__(self):
        return f"Medical Data - {self.id_patient.patient_first_name} {self.id_patient.patient_last_name}"

    class Meta:
        db_table = 'patient_medical_data'
        verbose_name = 'Patient Medical Data'
        verbose_name_plural = 'Patient Medical Data'